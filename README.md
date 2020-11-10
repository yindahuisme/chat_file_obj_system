# chat_file_obj_system
首先，这是一个基于maven构建的java项目,其中涉及到了对日志文件的按行读取和解析。因此，要熟悉java的文件io流操作,要做一个完备的日志系统。还要有对日期,浮点数据格式化的能力。然后这是一个网络项目客户端需要与服务器对接数据。需要对java的套接字编程，多线程以及网络的输入输出流有一定的了解。还要管理多个客户端连接的情况，因此也要对集合,线程池等了解了解，分析日志以及自定义网络协议需要对字符串进行分割，匹配等，因此还要了解常用的字符串分割以及正则表达式。这是一个比较复杂的系统。运用一些编程中的设计模式至关重要，比如说工厂模式，装饰模式，单例模式，由于这次系统采用的图形界面是基于java的AWT的，要了解一下。总的来说，本系统不仅仅完成了系统的对象解析以及传输任务。本系统倾向于实现一个消息文件发送与接收的聊天系统。这样在服务器与客户端交互的过程中发生了错误时，可以及时的进行沟通。在这次项目中。用到了大量的输入输出流。例如，文件解析的文件读取流,缓冲流。套接字的输入输出流。同时在管理都多个客户端连接时，用到了线程池的技术。对程序的并发处理有了更深刻的了解。文件流，二进制流，对象流的循环处理，也有了更深入的体会。本次项目中，我们用套接字编程的方式实现了一整套的消息对象文件的发送与接收协议。提供了密码请求服务。客户端发送不同的消息协议。实现了客户端与服务器连接的三种状态，即消息发送状态，对象流处理状态与文件流处理状态。客户端与服务器根据这三种状态的不同，可以。通过一个套接字通道实现多种不同的发送形式以及处理形式。实现了套接字复用。
