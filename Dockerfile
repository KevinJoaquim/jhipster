FROM nginx:latest
RUN sed -i 's/nginx/kev/g' /usr/share/nginx/html/index.html
EXPOSE 80
