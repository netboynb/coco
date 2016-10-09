## 1  长连接资源管理
   客户端定时向服务端发送心跳，来维系长连接，服务端使用netty的 IdleStateHandler 来管理tcp链接，在指定时间内未进行read/write/allRW事件的处理。
