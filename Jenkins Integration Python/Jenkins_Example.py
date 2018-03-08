import jenkins
import requests

PATH = 'sample.json'
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
#print(all_jobs)

build_number = server.get_job_info('Hello')['lastSuccessfulBuild']['number']
print("myjob info",build_number)

#debuginfo = server.debug_job_info("Hello")
#print("debug info\n",debuginfo)
#print(jobinfo)
#print(server.get_build_info("Hello"))

#r = requests.get("https://raw.githubusercontent.com/chetan-mehta707/Machine-Learning-Projects/master/sample.json")
#print(r.text)

consoleop = server.get_build_console_output('Hello',build_number)
#print(consoleop)

buildinfo = server.get_build_info('Hello',build_number)['changeSet']['items']
print(buildinfo)

for ele in buildinfo:
    print("Affected file list",ele['affectedPaths'])
    for innerele in ele['affectedPaths']:
        print("Affected File:",innerele)
        if innerele == PATH :
            r = requests.get("https://raw.githubusercontent.com/chetan-mehta707/Machine-Learning-Projects/master/sample.json")
            print(r.text)
