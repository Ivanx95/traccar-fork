#!/usr/bin/env python3

import sys
import os
import xml.etree.ElementTree
import urllib
import urllib.request as urllib2
import json
import socket
import time
import threading
import re

messages = {
    'gps103' : 'imei:1234,help me,1201011201,,F,120100.000,A,6000.0000,N,13000.0000,E,0.00,;',
    'gps103' : 'imei:1234,help me,1201011201,,F,120200.000,A,6100.0000,N,13000.0000,E,0.00,;',
    'gps103' : 'imei:1234,help me,1201011201,,F,120300.000,A,6200.0000,N,13000.0000,E,0.00,;',
    'gps103' : 'imei:1234,help me,1201011201,,F,120400.000,A,6300.0000,N,13000.0000,E,0.00,;',
    'gps103' : 'imei:1234,help me,1201011201,,F,120400.000,A,6400.0000,N,13000.0000,E,0.00,;',
    'gps103' : 'imei:1234,help me,1201011201,,F,120100.000,A,6000.0000,N,13000.0000,E,0.00,;',
    'gps103' : 'imei:1234,help me,1201011201,,F,120100.000,A,6000.0000,N,13000.0000,E,0.00,;',
    'gps103' : 'imei:1234,help me,1201011201,,F,120100.000,A,6000.0000,N,13000.0000,E,0.00,;',
 }


positions = [
#     'imei:1234,help me,1201011201,,F,120100.000,A,8000.0000,N,15000.0000,E,2.00,;',
#     'imei:1234,help me,1201011201,,F,120100.000,A,6000.0000,N,13000.0000,E,0.00,;',
#     'imei:1234,help me,1201011201,,F,130100.000,A,7000.0000,N,14000.0000,E,1.00,;',
#     'imei:1234,help me,1201011201,,F,140100.000,A,8000.0000,N,23000.0000,E,2.00,;',
#     'imei:1234,help me,1201011201,,F,150100.000,A,9000.0000,N,15000.0000,E,9.00,;',
'*HQ,2076818183,V1,000657,A,1757.7981,N,10212.9188,W,000.00,000,180825,FFF7F9FF,000,00,00000,0000#',
'*HQ,2076818183,V1,001157,A,1757.7981,N,10212.9188,W,000.00,000,180825,FFF7F9FF,000,00,00000,0000#',
'*HQ,2076818183,V1,001330,A,1757.7981,N,10212.9188,W,003.79,278,180825,FFF7F9FF,000,00,00000,0000#',
'*HQ,2076818183,V1,001830,A,1757.9462,N,10213.0125,W,011.16,241,180825,FFF7F9FF,000,00,00000,0000#',
'*HQ,2076818183,V1,002331,A,1757.9430,N,10213.6032,W,008.69,202,180825,FFF7F9FF,000,00,00000,0000#',
'*HQ,2076818183,V1,002733,A,1357.8834,N,10213.6203,W,003.15,102,180825,FFF7F9FF,000,00,00000,0000#',
'*HQ,2076818183,V1,003234,A,1454.2179,N,10212.7070,W,015.98,357,180825,FFF7F9FF,000,00,00000,0000#',
'*HQ,2076818183,V1,003734,A,1651.6092,N,10210.6279,W,000.00,343,180825,FFF7F9FF,000,00,00000,0000#'

 ]

baseUrl = 'https://www.advanced-gps.com'
user = { 'email' : 'larralde.ortiz.jaisen@gmail.com', 'password' : 'Lzczacgdlnyccdmx95$' }

connection = ""
debug = '-v' in sys.argv

def load_ports():
    ports = {}
    dir = os.path.dirname(os.path.abspath(__file__))
    with open(dir + '/../src/main/java/org/traccar/config/PortConfigSuffix.java', 'r') as file:
        content = file.read()
    pattern = re.compile(r'PORTS\.put\("([^"]+)",\s*(\d+)\);')
    matches = pattern.findall(content)
    ports = {protocol: int(port) for protocol, port in matches}
    if debug:
        print('\nports: {ports!r}\n')
    return ports

# def login():
#     request = urllib2.Request(baseUrl + '/api/session')
#     response = urllib2.urlopen(request, urllib.parse.urlencode(user).encode())
#     if debug:
#         print(f'\nlogin: {json.load(response)!r}\n')
#     return response.headers.get('Set-Cookie')

def remove_devices(cookie):
    request = urllib2.Request(baseUrl + '/api/devices')
    request.add_header('Cookie', cookie)
    response = urllib2.urlopen(request)
    data = json.load(response)
    if debug:
        print(f'\ndevices: {data!r}\n')
    for device in data:
        request = urllib2.Request(baseUrl + '/api/devices/' + str(device['id']))
        request.add_header('Cookie', cookie)
        request.get_method = lambda: 'DELETE'
        response = urllib2.urlopen(request)

def add_device(cookie, unique_id):
    request = urllib2.Request(baseUrl + '/api/devices')
    request.add_header('Cookie', cookie)
    request.add_header('Content-Type', 'application/json')
    device = { 'name' : unique_id, 'uniqueId' : unique_id }
    response = urllib2.urlopen(request, json.dumps(device).encode())
    data = json.load(response)
    return data['id']

def send_message(port, message):
    print(message)
    global connection
    if connection =="":
     connection = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
     connection.connect(('www.advanced-gps.com', port))

    connection.send(message.encode('ascii'))
    time.sleep(50)



def get_protocols(cookie, device_id):
    params = { 'deviceId' : device_id, 'from' : '2000-01-01T00:00:00.000Z', 'to' : '2050-01-01T00:00:00.000Z' }
    request = urllib2.Request(baseUrl + '/api/positions?' + urllib.parse.urlencode(params))
    request.add_header('Cookie', cookie)
    request.add_header('Content-Type', 'application/json')
    request.add_header('Accept', 'application/json')
    response = urllib2.urlopen(request)
    protocols = []
    for position in json.load(response):
        protocols.append(position['protocol'])
    return protocols

if __name__ == "__main__":
    ports = load_ports()

#     cookie = login()
#     remove_devices(cookie)

#     devices = {
#
#         '13' : add_device(cookie, '13')
#     }

    all = set(ports.keys())
    protocols = set(messages.keys())

    print(f'Total: {len(all)}')
    print(f'Missing: {len(all - protocols)}')
    print(f'Covered: {len(protocols)}')

    for pos in positions:
        send_message(ports['h02'], pos)


#     for device in devices:
#         protocols -= set(get_protocols(cookie, devices[device]))

    connection.close()
    print(f'Success: {len(messages) - len(protocols)}')
#
#     if protocols:
#         print(f'\nFailed: {list(protocols)!r}')
