# Nivio React Frontend

## Development

1) Start nivio

Run

    mvn package
    java -jar DEMO=1 java -Dserver.port=8081 -jar target/nivio.jar

2) Start the app

Run

    cd src/main/app
    yarn run start
    
then go to the app and provide the correct host

    http://localhost:3000/?host=http://localhost:8081
    

    
## Production

    yarn build

or

    mvn package
    