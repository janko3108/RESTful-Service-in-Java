# Build
mvn clean package && docker build -t com.rit/leskovac-janko-p2 .

# RUN

docker rm -f leskovac-janko-p2 || true && docker run -d -p 8080:8080 -p 4848:4848 --name leskovac-janko-p2 com.rit/leskovac-janko-p2 