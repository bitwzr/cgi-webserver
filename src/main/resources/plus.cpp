#include <iostream>
#include <string>
#include <cstdlib>
#include <stdlib.h>
#include <stdio.h>
#include <cstring>
#include <cmath>

using namespace std;

char header[] =
	        "Content-type:text/html\r\n\r\n"
	        "<html>\n"
	        "<head>\n"
	        "<title>Testing Post</title>\n"
	        "</head>\n"
	        "<style>\n"
            "body{ background-image: url(\"calculator.jpeg\"); }\n"
            "</style>\n"
	        "<body>\n";
char tail[] =
            "<br/>\n"
            "<a href=\"/Calculator.html\">BACK</a>"
            "</body>\n"
            "</html>\n";

// +: 0; -: 1; *: 2; /: 3; %: 4;
void generate(double m, double n, int op, double result, char* postmsg=NULL) {
    cout << header << "<h1>Here is the result!</h1><br/>\n";
	char ops[5] = {'+', '-', '*', '/', '%'};
    cout << m << " " << ops[op] << " " << n << " = " << result << "<br/>" << endl;
    cout << tail;
}

void report_error(char* poststr, char* msg) {
    cout << header << "<h1>ERROR</h1>" << endl;
    cout << msg << "<br/>" << endl;
    cout << "Original post msg: <br/> \n";
    cout << "----------------------------<br/>" << endl;
	cout << poststr << endl;
    cout << tail;
}

int main() {
	char *lenstr =getenv("CONTENT_LENGTH");
	if(lenstr==NULL){
		cout<<"Error, CONTENT_LENGTH should be entered!"<<"<br/>";
	}
	else{
	  	int len=atoi(lenstr);
	  	char poststr[100];
	  	fgets(poststr, len + 1, stdin);
	  	cout<<"poststr:"<<poststr<<"<br/>";
	  	char errormsg[] = "ERROR: bad parameters ";
        double m, n;
        int op;
	  	if(sscanf(poststr,"m=%lf&op=%d&n=%lf",&m,&op,&n)!=3) {
            report_error(poststr, errormsg);
	  	}
		else {
		    double result = 0;
		    if (op == 0) {
		        result = m + n;
		    } else if (op == 1) {
		        result = m - n;
		    } else if (op == 2) {
		        result = m * n;
		    } else if (op == 3) {
		        double epos = 1e-7;
		        if (abs(n) < epos) {
		            report_error(poststr, "ERROR: divided by zero");
                    return 0;
		        }
		        else {
		            result = m / n;
		        }
		    }
		    else if (op == 4) {
		        if (int(n) == 0) {
		            report_error(poststr, "ERROR: divided by zero");
                    return 0;
		        }
		        result = (int)m % (int)n;
		    }
            generate(m, n, op, result, poststr);
	  	}
	}
	return 0;
} 
