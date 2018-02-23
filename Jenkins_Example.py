import jenkins
import requests
username = "chetan"
password = "chetan"
server = jenkins.Jenkins('http://localhost:8080/',username,password)
user = server.get_whoami()
print(user)
version = server.get_version()
print(version.__class__)
jobs = server.get_jobs()
print(jobs)
all_jobs = server.get_all_jobs()
print(all_jobs)

r = requests.get("https://raw.githubusercontent.com/chetan-mehta707/Machine-Learning-Projects/master/sample.json")
print(r.text)
