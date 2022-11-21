./gradlew :buildImage
docker load < build/jib-image.tar

container_id=$(docker ps | grep 'wikipedia-links-preview' | cut -d' ' -f1)

docker stop "$container_id"
docker rm "$container_id"

# from build.gradle.kts
docker run -d -p 80:80 wikipedia-links-preview:0.0.4
