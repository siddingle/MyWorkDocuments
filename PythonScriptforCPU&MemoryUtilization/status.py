 	


import platform
import psutil
import shutil
import socket
import json
import requests
import datetime
import uuid

print("Hello World!")




def generateJSON():
    total, used, free = shutil.disk_usage("/")
    osDetails = {}
    osDetails['timestamp'] = datetime.datetime.now().isoformat()
    osDetails['ComputerName'] = platform.node()
    osDetails['IPAddress'] = socket.gethostbyname(socket.gethostname())
    osDetails['OS-Name'] = platform.system()
    osDetails['CPU'] = {}
    osDetails['CPU']["physical"] = psutil.cpu_count(logical=False)
    osDetails['CPU']["logical"] = psutil.cpu_count(logical=True)
    osDetails['CPU']["utilization"] = psutil.cpu_percent(interval=1)
    osDetails['RAM'] = {}
    osDetails['RAM']["total"] = round(psutil.virtual_memory().total/1000000000, 2)
    osDetails['RAM']["available"] = round(psutil.virtual_memory().available/1000000000, 2)
    osDetails['RAM']["used"] = round(psutil.virtual_memory().used/1000000000, 2)
    osDetails['HDD'] = {}
    osDetails['HDD']["total"] = (total // (2**30))
    osDetails['HDD']["available"] = (free // (2**30))
    osDetails['HDD']["used"] = (used // (2**30))
    return osDetails


def pushOSDetails(a_osDetails):
    api_url = "http://172.16.100.20:9200/serverhealth/_doc/" + str(uuid.uuid4())
    username = "elastic"
    password = "Meeting#4321"
    headers = {'Content-Type': 'application/json'}
    
    print("pushing health json")
    response = requests.post(api_url, json=a_osDetails, headers=headers, auth=(username, password))
    print(response)

def pushOSDetails1(a_osDetails):
    api_url = "http://172.16.100.20:9080/logger/api/pushOSDetails"
    headers = {'s': 'f4b45be1-9bed-4329-a991-2edaf47a4472'}
    
    print("pushing health json")
    response = requests.post(api_url, json=a_osDetails, headers=headers)
    print(response.json()["msg"])

osDetails = generateJSON()
json_data = json.dumps(osDetails, indent=4, sort_keys=True, default=str)
print(json_data);

pushOSDetails(osDetails)
pushOSDetails1(osDetails)

