REM Create the server's private key
openssl genrsa -des3 1024 > ./certs/localhost.key

REM Create the server's private key without a password
REM openssl genrsa 1024 > ./certs/localhost.key

REM Create the CSR file
openssl req -new -key ./certs/localhost.key -out ./certs/localhost.csr -config openssl.cnf

REM Create the self-signed certificate
openssl req -x509 -key ./certs/localhost.key -in ./certs/localhost.csr -out ./certs/localhost.crt