# uscis-case-status


Get the uscis case status by simply sending an sms to a phone number. This project is uses Twilio SMS feature to send reply to users. 


Note:- This project is only for demo.  

disclaimer:- text/sms message rates may apply

### Follow the steps use this service:
1. You can send case / receipt number to this number using SMS
2. You should get reply message like " Your status is :" if you sent a valid case number

### Steps to run in local:
1. sbt clean run

### Steps to Build Docker Image:
1. Run below command to build JAR file
```shell 
sbt clean assembly
```
2. Run below command to build Docker Image
```shell 
export ImageTag=`cat DockerVersion`
docker build -t mallanna4u/usciscasestatus:${ImageTag} .
docker push mallanna4u/usciscasestatus:${ImageTag}
```
