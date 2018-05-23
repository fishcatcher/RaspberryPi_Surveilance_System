%% Jarett Sullivan
% ME 319
% Final Project

clear;
clc;

%I.)
%a.) Simpsons
q = quad('(1./x.^4)',1,20000)

%b.) Lobatto
r = quadl('(1./x.^4)',1,20000)

%c.)
x = linspace(1,20000,20000);
y = 1./x.^4
s = trapz(x,y)

%d.)
syms x
u = 1./x.^4;
t = int(u,1,20000)
% Simpsons and Lobatto quadratures produce the same result, 'int' function
% gives the exact (same) answer as a fraction. Trapezoidal is similar, but
% inaccurate estimate

%% II.)
clear;
clc;
syms x
n = 0
f(x) = (exp(x)-1)/x;
f = 0
for  n = 1:10
    f(x) = (diff(f,n))*x/(factorial(n));
end
    



%% Comparison
clear;
clc;
syms x
taylor((exp(x)-1)/x,'order',10)

%% Mupad
%III.)
clear;
clc;

%%
%IV.)
clear;
clc;
%a.)

n=[1,3,5,7,9,11,13,15,17,19];
term=abs((2./n).*sin(n*pi/2).*(sinh(n*pi/2)./sinh(n*pi)));
format short e
disp(term'/term(1))
%magnitude gets smaller
%% b.)
clear;clc;
t1=100;t2=250;w=1;l=1;x=w/2;y=l/2;
change=100;term=0;n=1;m=1;

while abs(change) > .01
    fterm=term+(2/n)*sin((n*pi*x)/l)*(sinh((n*pi*y)/l)/sinh((n*pi*w)/l));
    if n>1
        change=((abs(fterm)-abs(term))/abs(term));
    end
    term=fterm;
    n=n+2;
end
w=(2/pi)*fterm;
tmid=(t2-t1)*w+t1;
disp('The temp in the middle is:')
disp(tmid)
disp('The number of series terms req is:')
disp(n)

% (100*3+250)/4=137.5, average of all sides
%% c.)
clear;clc;
t_1=100;
t_2=250;
i=0;

W=1;
L=1;
change=100;
term=0;

for x=0:.2:1
    i=i+1;
    j=0;
    for y=0:.2:1
        j=j+1;
         change=100;
         n=1;
         term=0;
             while change>0.01
                    w=term + ((2/pi)*(2/n)*sin(n*pi*x./L)*(sinh(n*pi*y./L)/sinh(n*pi*W/L)));
                if n>1
                change=abs((abs(w-term))/term);
                end
                term=w;
                if change> 0.01
                n=n+2;
                end
             end
        T(j,i)=(t_2-t_1)*(w)+t_1;  
        if T(j,i)>=193
            T(j,i)=200
        end
    end
end
mesh(T)
xlabel('Length')
ylabel('Width')
zlabel('Temperature')

%%
%V.) Simulink
clear;
clc;









