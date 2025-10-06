#!/bin/bash

sudo systemctl stop postgresql

sudo docker compose -f ../docker-compose.yml up
