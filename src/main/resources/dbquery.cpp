#include <iostream>
#include <string>
#include <cstdlib>
#include <stdlib.h>
#include <stdio.h>
#include <cstring>
#include <sstream>
#include "MyDB.h"

using namespace std;

char header[] =
	        "Content-type:text/html\r\n\r\n"
	        "<html>\n"
            "<head\n"
            "<meta charset=\"UTF-8\">\n"
            "<title>RESULT</title>\n"
            " <style>\n"
            "  * {\n"
            "    margin: 0;\n"
            "    padding: 0;\n"
            "  }\n"
            "  html {\n"
            "    height: 100%;\n"
            "  }\n"
            "  body {\n"
            "    height: 100%;\n"
            "  }\n"
            "  .container {\n"
            "    height: 100%;\n"
            "    background-image: linear-gradient(to right, #999999, #123121);\n"
            "  }\n"
            "  .login-wrapper {\n"
            "    background-color: bisque;\n"
            "    width: 358px;\n"
            "    height: 588px;\n"
            "    border-radius: 15px;\n"
            "    padding: 0 50px;\n"
            "    position: relative;\n"
            "    left: 50%;\n"
            "    top: 50%;\n"
            "    transform: translate(-50%,-50%);\n"
            "  }\n"
            "  .header {\n"
            "    font-size: 38px;\n"
            "    font-weight: bold;\n"
            "    text-align: center;\n"
            "    line-height: 200px;\n"
            "  }\n"
            "  .input-item {\n"
            "    display: block;\n"
            "    width: 100%;\n"
            "    margin-bottom: 20px;\n"
            "    border: 0;\n"
            "    padding: 10px;\n"
            "    border-bottom: 1px solid rgb(128,125,125);\n"
            "    font-size: 15px;\n"
            "    outline: none;\n"
            "  }\n"
            "  .input-item::placeholder {\n"
            "    text-transform: uppercase;\n"
            "  }\n"
            "  .btn {\n"
            "    text-align: center;\n"
            "    padding: 10px;\n"
            "    width: 100%;\n"
            "    margin-top: 40px;\n"
            "    background-image: linear-gradient(to right,#a6c1ee, #123231);\n"
            "    color: #fff;\n"
            "  }\n"
            "  .msg {\n"
            "    text-align: center;\n"
            "    line-height: 88px;\n"
            "  }\n"
            "  a {\n"
            "    text-decoration-line: none;\n"
            "    color: #abc1ee;\n"
            "  }\n"
            "</style>\n"
            "</head>\n"
            "<body>\n"
            "<div class=\"container\">\n"
            "  <div class=\"login-wrapper\">\n"
            "   <div class=\"header\">Result</div>\n"
            "   <div class=\"form-wrapper\">\n";

char tail[] = "</div> </div> </div> </body> </html>";

void generate(char * studentId) {
    cout << header << "<h3>" << studentId << "</h3>";
    MyDB db;
    db.initDB("localhost","root","root","cgi");
    cout << "<p>";
    stringstream str;
    str << "SELECT \"</br><b>Name</b>\", name, \"<br/><b>Class</b>\", classno from student where number = ";
    str << studentId;
    db.exeSQL(str.str());
    cout << "</p>";
    cout << "<br/>" << tail;
}

void report_error(char* poststr, char* errormsg) {
    cout << header << poststr << "<br/>" << errormsg << "<br/>" << tail;
}

int main() {
	char *lenstr =getenv("CONTENT_LENGTH");
	if(lenstr==NULL){
		cout<<"Error, CONTENT_LENGTH should be entered!"<<"<br/>";
	}
	else{
	  	int len=atoi(lenstr);
	  	char poststr[120];
	  	fgets(poststr, len + 1, stdin);
	  	cout<<"poststr:"<<poststr<<"<br/>";
	  	char errormsg[] = "ERROR: bad parameters ";
        char studentId[100];
        cout << "<!DOCTYPE html>" << endl;
	  	if(sscanf(poststr,"id=%s", studentId)!=1) {
            report_error(poststr, errormsg);
            return 0;
	  	}

        generate(studentId);
	}
	return 0;
}
