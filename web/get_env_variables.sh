#!/bin/sh

# Script to export Heroku environment variables into the local shell
# Should work with any Heroku setup
# Run "source get_env_variables.sh" to get these into your shell

cd ..

name=""
value=""
for x in `heroku config | tail -n +2`; do
  if [ "$name" = "" ] 
  then
    name="${x%?}" # remove trailing : 
  else
    value=$x
    export $name=$value
    name=""
    value=""
  fi
done

cd web

