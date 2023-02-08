#!/bin/bash

scp target/fincost-1.0-SNAPSHOT.jar root@185.229.66.23:/root/bot/fincost
scp run.sh root@185.229.66.23:/root/bot/fincost
scp src/main/resources/prod.properties root@185.229.66.23:/root/bot/fincost
