fuser -k 10501/tcp || true
export $(cat production-portfolio/.env | xargs -d '\n')
SPRING_PROFILES_ACTIVE=$SPRING_PROFILES_ACTIVE java -jar production-portfolio/libs/portfolio-0.0.1-SNAPSHOT.jar \
    --server.contextPath=/prod/portfolio \
    --server.port=10501 \
    --spring.application.name=portfolio \
    --grpc.client.identity-provider-grpc-server.address=static://127.0.0.1:10500 \
    --grpc.client.identity-provider-grpc-server.enableKeepAlive=true \
    --grpc.client.identity-provider-grpc-server.keepAliveWithoutCalls=true \
    --grpc.client.identity-provider-grpc-server.negotiationType=plaintext