# sample server
##  just play how to use the framework to work 
##  three model example: only restful model, only rpc model ,both restful and rpc model

###  both restful and rpc model

1  start server

```
just run

sample-server's

com.ms.coco.rpc.server.CocoSampleServer.main(String[])
```
  you must make sure the "spring.xml" file's "cocoServer" bean's is

```
        <property name="useRestFul" value ="true"/>
        <property name="useRpc" value ="true"/>

```

 here ,if both of the is true,is meaning start server with restful and rpc

+++++++++++
  
```
        <property name="useRestFul" value ="false"/>
        <property name="useRpc" value ="true"/>

```
meaning just start server with Rpc model ,no restful model

+++++++++++
  
```
        <property name="useRestFul" value ="true"/>
        <property name="useRpc" value ="false"/>

```
meaning just start server with restful model ,no Rpc model



### just only simple restful model

1  start restful server

``` 
run
sample-server 's
com.ms.coco.rest.server.CocoRestfulServerDemo
```

2  use get post put delete to test

```
get      localhost:8089/success/0.json?echo=i+am+an+message
post     localhost:8089/success/0.json?echo=i+am+an+message
put      localhost:8089/success/0.json?echo=i+am+an+message
delete   localhost:8089/success/0.json?echo=i+am+an+message
```

###  test rpc client example

you will find five example class in the "sample-client" project ,just run them
