#!/bin/sh

keytool -genkey -v -keystore key.jks -alias fisherman -keyalg RSA -keysize 2048 -validity 10000
