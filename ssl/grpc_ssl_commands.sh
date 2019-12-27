#1
openssl genrsa -passout pass:password -des3 -out ca.key 4096
#2
SERVER_CN=localhost
#3
openssl req -passin pass:password -new -x509 -days 365 -key ca.key -out ca.crt -subj "/CN=${SERVER_CN}"
#4
openssl genrsa -passout pass:password -des3 -out server.key 4096
#5
openssl req -passin pass:password -new -key server.key -out server.csr -subj "/CN=${SERVER_CN}"
#6
openssl x509 -req -passin pass:password -days 365 -in server.csr -CA ca.crt -CAkey ca.key -set_serial 01 -out server.crt
#7
openssl pkcs8 -topk8 -nocrypt -passin pass:password -in server.key -out server.pem