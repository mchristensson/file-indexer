
# Generate cert - The location can be revealed by openssl version -d 
RUN openssl req -x509 -nodes -days 365 -newkey rsa:2048  -subj "/C=SE/ST=Stockholm/L=Stockholm/O=Urax/CN=flactrax.int" -keyout /etc/ssl/certs/private.key -out /etc/ssl/certs/public.crt

# Cretae diffie-hellman group
RUN openssl dhparam -out /etc/ssl/certs/dhparam.pem 2048

# Generate keystore
RUN openssl pkcs12 -export -in /etc/mycerts/public.crt -inkey /etc/mycerts/private.key -out /etc/mycerts/keystore.p12 -name fookeystorealias -passin pass:foo -passout pass:foo
