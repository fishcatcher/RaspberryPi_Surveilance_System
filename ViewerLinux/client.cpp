#include <opencv2/core/core.hpp>

#include "opencv2/opencv.hpp"

#include <iostream>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <pthread.h>
#include <errno.h>
#include <sys/types.h>

#include <sys/stat.h>

#include <fcntl.h>
#include <signal.h>



//server client
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>


#include <time.h>
#include <stdint.h>

using namespace cv;

#define handle_error_en(en, msg) \
               do { errno = en; perror(msg); exit(EXIT_FAILURE); } while (0)


#define handle_error(msg) \
               do { perror(msg); exit(EXIT_FAILURE); } while (0)

struct set_frame_size {
	int width;
	int height;
	int channel;
	int fps;
};



#define INITIAL_CONNECTION (0)
#define SEND_PACKETS (1)
#define CLOSE_GARAGE (3)
#define CLOSE_CONNECTION (4)

#define MAX_ERROR_TRIES 5
#define MAX_THREAD 2

const int PORTS[5] = {5000, 5001, 5002, 5003, 5004};	
bool thread_quit = false;

int is_exit = 0;
int key = -1;

uint8_t request = 0;

pthread_mutex_t lock = PTHREAD_MUTEX_INITIALIZER;

void* print_dynamic_msg_main(void* arg)

{
	std::cout << "[*]Processing images   ";
	while (!thread_quit) {
		std::cout << "\b\b\b   ";
		sleep(1);
		fflush(stdout);
		std::cout << "\b\b\b.  ";
		sleep(1);
		fflush(stdout);
		std::cout << "\b\b\b.. ";
		sleep(1);
		fflush(stdout);
		std::cout << "\b\b\b...";
		sleep(1);
		fflush(stdout);
	}
	return NULL;
}



void sig_handler(int signo)
{
	is_exit = 2;
}

int main(int argc, char *argv[])
{
	if (argc != 2) {
		std::cout << "Usage: ./client <ip>\n";
		return -1;
	}
//=====================================
	struct sigaction act;
	act.sa_handler = sig_handler;
	act.sa_flags = 0;
	sigemptyset(&act.sa_mask);
	sigaction(SIGINT, &act, NULL);
//=====================================
	std::cout << "\n*==============================*\n";
//=====================================
	int sock_fd = 0;
	int size = 0;
	int val;
	int bytes = 1;
	int s, r;
	int i = 0;
	uchar *data;
	struct sockaddr_in client_addr;
//=======================================
	do {
		sock_fd = socket(AF_INET, SOCK_STREAM, 0);
		if (sock_fd == -1) {
			std::cout << "Error on CREATING socket --> trying again...\n";
			i++;
			if (i == MAX_ERROR_TRIES) {
				std::cout << "Error on CREATING socket --> terminating...\n";
				std::cout << "[-] Server Main Ends... \n\n";
				return -1;
			}
		}
	} while (sock_fd == -1);

	val = inet_pton(AF_INET, argv[1], &client_addr.sin_addr);
	
	if (val <= 0) {
		handle_error("inet_pton");
		return -1;
	}
	
	do {
		client_addr.sin_family = AF_INET;
		client_addr.sin_port = htons(PORTS[i]);
		val = connect(sock_fd, (struct sockaddr*)&client_addr, sizeof(client_addr));
		i++;
		if (val == -1) {
			if (i == MAX_ERROR_TRIES) {
				std::cout << "Error, connecting to server --> terminating...\n";
				return -1;
			}
			else
				printf("Error, connecting to server --> trying port[%d]\n", PORTS[i]);	
		}
	} while (val == -1);

	printf("[*]Connection established [%s] [%d]\n",argv[1], PORTS[i-1]);
	std::cout << "[*]Press any key to quit\n";

//==========================
	pthread_t id;
	pthread_attr_t attr;
	pthread_attr_init(&attr);
	pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
	pthread_create(&id, &attr, &print_dynamic_msg_main, NULL);

//==========================
	set_frame_size frame_size;
	namedWindow("Garage", 1);
	request = INITIAL_CONNECTION;
	do {
		s = send(sock_fd, &request, sizeof(request), MSG_NOSIGNAL);
		switch (request) {
			case INITIAL_CONNECTION: {
				r = recv(sock_fd, &frame_size, sizeof(frame_size), 0);
				data = new uchar[frame_size.width * frame_size.height * frame_size.channel];
				request = SEND_PACKETS;
				break;
			}
			case SEND_PACKETS: {
				r = recv(sock_fd, &size, sizeof(size), 0);
				for (int i=0; i<size; i+=bytes) {
					if ((bytes = recv(sock_fd, data+i, size-i, 0)) <= 0 ) {
						is_exit = 1;
						break;
					}
				}
				Mat compressed_frame = Mat(1, size, CV_8UC3, data);
				Mat decoded_frame = imdecode(compressed_frame, CV_LOAD_IMAGE_COLOR);
				//
				resize(decoded_frame, decoded_frame, Size(640,480), 0, 0, INTER_LINEAR);
				//
				cv::imshow("Garage", decoded_frame);
				key = cv::waitKey(1000/frame_size.fps);
				decoded_frame.release();
				request = SEND_PACKETS;
				break;
			}
		}
	} while (key == -1 && !is_exit && s > 0 && r > 0);

	thread_quit = true;
	cv::destroyWindow("Garage");
	delete data;
	close(sock_fd);
	std::cout << "\n[*]Connection closed by [";
	if (is_exit == 1)
		std::cout << "SERVER]\n";
	else
		std::cout << "USER]\n"; 
	std::cout << "*==============================*\n\n";
	return 0;
}

	



