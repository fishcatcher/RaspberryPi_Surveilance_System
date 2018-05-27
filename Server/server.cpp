#include <stdio.h>

#include <iostream>
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/opencv.hpp"

//server client
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

//sleep
#include <unistd.h>

//threading
#include <pthread.h>

//signal
#include <signal.h>

//byte int
#include <stdint.h>

//time
#include <time.h>

//standard definition
#include "userdef.h"

using namespace cv;
using namespace std;

#define COMPRESSION_METHOD (CV_IMWRITE_JPEG_QUALITY)
#define COMPRESSION_QUALITY (80)

struct set_frame_size {
	int width;
	int height;
	int channel;
	int fps;
} frame_size = {
	.width = 640,
	.height = 480,
	.channel = 3,
	.fps = 30
};

const int PORTS[5] = {5000, 5001, 5002, 5003, 5004};

bool is_exit = false;
bool server_start = false;
uchar *img_buffer = NULL;
int compressed_frame_size;
int listen_fd;
unsigned int frame_id = 0;

pthread_t threads[MAX_THREAD];

pthread_rwlock_t rwlock = PTHREAD_RWLOCK_INITIALIZER;


void *get_frame_main(void*);
void *server_main(void*);
void *send_frame_main(void*);
void *step_frame(void*);
void get_system_time(char*);

void sig_handler(int signo)
{
	is_exit = !is_exit;

	shutdown(listen_fd, SHUT_RDWR);
	if (signo == SIGINT)
		printf("\n");
}

int main(int argc, char* argv[])
{
	cout << "\n";
	cout << GREEN << "[+]Main Thread Starts...\n\n" << RESET;
	
//========================================
	struct sigaction act;
	act.sa_handler = sig_handler;
	act.sa_flags = 0;
	sigemptyset(&act.sa_mask);
	sigaction(SIGINT, &act, NULL);
	sigaction(SIGUSR1, &act, NULL);
//========================================	
	char system_time_start[BUF_SIZE];
	char system_time_stop[BUF_SIZE];
	
	get_system_time(system_time_start);
//========================================	
	
	
	pthread_create(&threads[0], NULL, &get_frame_main, NULL);

	pthread_create(&threads[1], NULL, &server_main, NULL);
	
	for (int i=0; i<MAX_THREAD; i++) {
		pthread_join(threads[i], NULL);
	}
	
	pthread_rwlock_destroy(&rwlock);
	
//========================================
	get_system_time(system_time_stop);
//========================================
	cout << CYAN << "\t[*]Version\tv1.2\n" << RESET;
	cout << CYAN << "\t[*]Start\t" << system_time_start << RESET;
	cout << CYAN << "\t[*]Stop \t" << system_time_stop << RESET;
	cout << "\n";
	cout << RED << "[-]Main Thread Ends\n\n" << RESET;
	
	return 0;
}

void *get_frame_main(void *arg)
{
	cout << GREEN << "[+]Get_Frame_Main Thread Starts...\n\n" << RESET;
	
	bool is_framed;
	VideoCapture cap(0);
	
	if (!cap.isOpened()) {
		system("sudo modprobe bcm2835-v4l2");
		cap.open(0);
		if (!cap.isOpened()) {
			cout << YELLOW << "\t[!]Error, opening camera --> terminating...\n" << RESET;
			cout << RED << "[-]Get_Frame_Main Thread Ends\n\n" << RESET;
			raise(SIGUSR1);
			return NULL;
		}
	}

	cap.set(CV_CAP_PROP_FRAME_WIDTH, frame_size.width);
	cap.set(CV_CAP_PROP_FRAME_HEIGHT, frame_size.height);
	cap.set(CV_CAP_PROP_FPS, frame_size.fps);
	
	
	pthread_rwlock_wrlock(&rwlock);
	img_buffer = new uchar[frame_size.width * frame_size.height * frame_size.channel];
	pthread_rwlock_unlock(&rwlock);
	
	
	while (!is_exit || !is_framed) {
		Mat frame;
		is_framed = cap.read(frame);
		
		if (is_framed) {
			pthread_rwlock_wrlock(&rwlock);
			memcpy(img_buffer, frame.data, frame_size.width * frame_size.height * frame_size.channel);
			frame_id++;
			pthread_rwlock_unlock(&rwlock);
			//waitKey(1000/frame_size.fps);
		}
		else
			cout << YELLOW << "\t[!]Error, capturing frame\n" << RESET;
	}
	
	pthread_rwlock_wrlock(&rwlock);
	delete img_buffer;
	img_buffer = NULL;
	pthread_rwlock_unlock(&rwlock);
	
	
	cout << RED << "[-]Get_Frame_Main Thread Ends\n\n" << RESET;
	return NULL;
}

void *server_main(void *arg)
{
	cout << GREEN << "[+]Server_Main Thread Starts... \n\n" << RESET;

	int t;
	int i = 0;

	struct sockaddr_in serv_addr;
	//struct sockaddr client_addr;

	do {
		listen_fd = socket(AF_INET, SOCK_STREAM, 0);
		i++;
		if (listen_fd == -1) {
			cout << YELLOW << "\t[!]Error on CREATING socket --> trying again...\n" << RESET;
			if (i == MAX_ERROR_TRIES) {
				cout << YELLOW << "\t[!]Error on CREATING socket --> terminating...\n" << RESET;
				cout << RED << "[-]Server_Main Thread Ends \n\n" << RESET;
				is_exit = true;
				return NULL;
			}
		}
	} while (listen_fd == -1);
	
	memset(&serv_addr, '0', sizeof(serv_addr));

	i = 0;
	
	do {
		serv_addr.sin_family = AF_INET;
		serv_addr.sin_addr.s_addr = htonl(INADDR_ANY);
		serv_addr.sin_port = htons(PORTS[i]);

		t = bind(listen_fd, (struct sockaddr*)&serv_addr, sizeof(serv_addr));
		i++;
		if (t == -1) {
			if (i == MAX_ERROR_TRIES) {
				cout << YELLOW << "\t[!]Error on BINDING socket --> terminating...\n" << RESET;
				cout << RED << "[-]Server Main Ends\n\n" << RESET;
				is_exit = true;
				return NULL;
			}
			else
				cout << YELLOW << "\t[!]Error on BINDING socket --> trying port [" << PORTS[i] << "]...\n" << RESET;
		}
		
		else {
			listen(listen_fd, 10);
			cout << BLUE << "\t[+]Listening...\n" << RESET;
			
			while(1) {
				int send_fd = accept(listen_fd, (struct sockaddr*)NULL, NULL);
				if (send_fd > 0) {
					pthread_t id;
					pthread_attr_t attr;
					pthread_attr_init(&attr);
					pthread_attr_setdetachstate(&attr, PTHREAD_CREATE_DETACHED);
					pthread_create(&id, &attr, &send_frame_main, (void*)&send_fd);
				} else break;
			}
		}
	} while (t == -1);
	
	close(listen_fd);
	
	cout << RED << "[-]Server_Main Thread Ends \n\n" << RESET;
	return NULL;
}

void *send_frame_main(void* arg)
{
	cout << BLUE << "\n\t\t\t[+]Connection accepted [";

	int send_fd = (*(int*)arg);
	int s = 1;
	int r = 1;
	int nsize, size;
	unsigned int id=0, old_id=0;
	uint8_t request = 0;
	
	uchar *data = new uchar[frame_size.width * frame_size.height * frame_size.channel];
	nsize = frame_size.width * frame_size.height * frame_size.channel;
	vector<int> compression_values;
	vector<uchar> encoded_frame;
	compression_values.push_back(COMPRESSION_METHOD);
	compression_values.push_back(COMPRESSION_QUALITY);


	cout << send_fd << "]\n" << RESET;
	
	do {
		pthread_rwlock_rdlock(&rwlock);
		if (img_buffer == NULL) {
			cout << YELLOW << "\t[!]Error, image buffer is unallocated\n" << RESET;
			break;
		}
		memcpy(data, img_buffer, nsize);
		id = frame_id;
		pthread_rwlock_unlock(&rwlock);

		/*if (old_id == id) {
			r = s = 1;
			continue;
		}
		*/		
		cv::Mat frame(frame_size.height,frame_size.width, CV_8UC3, data);		
		//resize(frame, frame, Size(frame_size.width, frame_size.height), 0, 0, INTER_LINEAR);
			
		//encode
		imencode(".jpg", frame, encoded_frame, compression_values);
		size = encoded_frame.size();
		
		r = recv(send_fd, &request, sizeof(request), 0);
		
		switch (request) {
			case INITIAL_CONNECTION:
				s = send(send_fd, &frame_size, sizeof(frame_size), MSG_NOSIGNAL);
				break;
				
			case SEND_PACKETS:
				s = send(send_fd, &size, sizeof(size), MSG_NOSIGNAL);	
				s = send(send_fd, &encoded_frame[0], size, MSG_NOSIGNAL);
				old_id = id;
				break;
				
			case CLOSE_GARAGE:
				break;
				
			case CLOSE_CONNECTION:
				break;
				
			default:
				cout << YELLOW << "\t[!]Error, request from client is unknown\n" << RESET;
				s = -1;
				break;
		}

	} while (s > 0 && r > 0);
	
	//all done
	cout << BLUE << "\t\t\t[-]Connection ended [" << send_fd << "]\n" << RESET;

	close(send_fd);
	
	delete [] data;
	
	return NULL;
}

void get_system_time(char *format)
{
	time_t current_time;
	struct tm *ti;
	
	time(&current_time);
	ti = localtime(&current_time);
	
	snprintf(format, BUF_SIZE, "%d %d %d %d:%d:%d\n", ti->tm_mon+1, ti->tm_mday, ti->tm_year+1900, ti->tm_hour, ti->tm_min, ti->tm_sec);
}