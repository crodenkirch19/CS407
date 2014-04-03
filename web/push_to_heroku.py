#!/usr/bin/python

import os
script_dir = os.path.dirname(os.path.realpath(__file__))
os.chdir(os.path.join(script_dir, ".."))
# os.system(r"git commit -a -m ")
os.system(r"git subtree push --prefix web heroku master")
