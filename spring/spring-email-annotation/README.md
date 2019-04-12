# spring 邮件发送（xml配置方式）
## 目录<br/>
<a href="#一说明">一、说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#11-项目结构说明">1.1 项目结构说明</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;<a href="#12-依赖说明">1.2 依赖说明</a><br/>
<a href="#二spring-email">二、spring email</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#21-邮件发送配置">2.1 邮件发送配置</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#22-新建邮件发送基本类">2.2 新建邮件发送基本类</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#23-邮件发送的测试">2.3 邮件发送的测试</a><br/>
## 正文<br/>


## 一、说明

### 1.1 项目结构说明

1. 邮件发送配置类为com.heibaiying.config下EmailConfig.java;
2. 简单邮件发送、附件邮件发送、内嵌资源邮件发送、模板邮件发送的方法封装在SpringMail类中；
3. 项目以单元测试的方法进行测试，测试类为SendEmail。



<div align="center"> <img src="https://github.com/heibaiying/spring-samples-for-all/blob/master/pictures/spring-email-annotation.png"/> </div>



### 1.2 依赖说明

除了spring的基本依赖外，需要导入邮件发送的支持包spring-context-support

```xml
 <!--邮件发送依赖包-->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context-support</artifactId>
    <version>${spring-base-version}</version>
</dependency>
 <!--模板引擎-->
        <!--这里采用的是beetl,beetl性能很卓越并且功能也很全面 官方文档地址 <a href="http://ibeetl.com/guide/#beetl">-->
<dependency>
    <groupId>com.ibeetl</groupId>
    <artifactId>beetl</artifactId>
    <version>2.9.7</version>
</dependency>
```



## 二、spring email

#### 2.1 邮件发送配置

```java
/**
 * @author : heibaiying
 * @description : 邮件发送配置类
 */

@Configuration
@ComponentScan(value = "com.heibaiying.email")
public class EmailConfig {

    /***
     * 在这里可以声明不同的邮件服务器主机，通常是SMTP主机,而具体的用户名和时授权码则建议在业务中从数据库查询
     */
    @Bean(name = "qqMailSender")
    JavaMailSenderImpl javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.qq.com");
        javaMailSender.setPassword("587");
        return javaMailSender;
    }

    /***
     * 配置模板引擎
     */
    @Bean
    GroupTemplate groupTemplate() throws IOException {
        //指定加载模板资源的位置 指定在classpath:beetl下-
        ClasspathResourceLoader loader = new ClasspathResourceLoader("beetl");
        //beetl配置 这里采用默认的配置-
        org.beetl.core.Configuration configuration = org.beetl.core.Configuration.defaultConfiguration();
        return new GroupTemplate(loader, configuration);
    }
}

```

#### 2.2 新建邮件发送基本类

```java
/**
 * @author : heibaiying
 * @description : 邮件发送基本类
 */
@Component
public class SpringMail {

    @Autowired
    private JavaMailSenderImpl qqMailSender;
    @Autowired
    private GroupTemplate groupTemplate;

    /**
     * 发送简单邮件
     * 在qq邮件发送的测试中，测试结果表明不管是简单邮件还是复杂邮件都必须指定发送用户，
     * 且发送用户已经授权不然都会抛出异常: SMTPSendFailedException 501 mail from address must be same as authorization user
     * qq 的授权码 可以在 设置/账户/POP3/IMAP/SMTP/Exchange/CardDAV/CalDAV服务 中开启服务后获取
     */
    public void sendTextMessage(String from, String authWord, String to, String subject, String content) {
        // 设置发送人邮箱和授权码
        qqMailSender.setUsername(from);
        qqMailSender.setPassword(authWord);
        // 实例化消息对象
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(content);
        try {
            // 发送消息
            this.qqMailSender.send(msg);
            System.out.println("发送邮件成功");
        } catch (MailException ex) {
            // 消息发送失败可以做对应的处理
            System.err.println("发送邮件失败" + ex.getMessage());
        }
    }

    /**
     * 发送带附件的邮件
     */
    public void sendEmailWithAttachments(String from, String authWord, String to,
                                         String subject, String content, Map<String, File> files) {
        try {
            // 设置发送人邮箱和授权码
            qqMailSender.setUsername(from);
            qqMailSender.setPassword(authWord);
            // 实例化消息对象
            MimeMessage message = qqMailSender.createMimeMessage();
            // 需要指定第二个参数为true 代表创建支持可选文本，内联元素和附件的多部分消息
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content);
            // 传入附件
            for (Map.Entry<String, File> entry : files.entrySet()) {
                helper.addAttachment(entry.getKey(), entry.getValue());
            }
            // 发送消息
            this.qqMailSender.send(message);
            System.out.println("发送邮件成功");
        } catch (MessagingException ex) {
            // 消息发送失败可以做对应的处理
            System.err.println("发送邮件失败" + ex.getMessage());
        }
    }


    /**
     * 发送带内嵌资源的邮件
     */
    public void sendEmailWithInline(String from, String authWord, String to,
                                    String subject, String content, File file) {
        try {
            // 设置发送人邮箱和授权码
            qqMailSender.setUsername(from);
            qqMailSender.setPassword(authWord);
            // 实例化消息对象
            MimeMessage message = qqMailSender.createMimeMessage();
            // 需要指定第二个参数为true 代表创建支持可选文本，内联元素和附件的多部分消息
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            // 使用true标志来指示包含的文本是HTML 固定格式资源前缀 cid:
            helper.setText("<html><body><img src='cid:image'></body></html>", true);
            // 需要先指定文本 再指定资源文件
            FileSystemResource res = new FileSystemResource(file);
            helper.addInline("image", res);
            // 发送消息
            this.qqMailSender.send(message);
            System.out.println("发送邮件成功");
        } catch (MessagingException ex) {
            // 消息发送失败可以做对应的处理
            System.err.println("发送邮件失败" + ex.getMessage());
        }
    }

    /**
     * 使用模板邮件
     */
    public void sendEmailByTemplate(String from, String authWord, String to,
                                    String subject, String content) {
        try {
            Template t = groupTemplate.getTemplate("template.html");
            t.binding("subject", subject);
            t.binding("content", content);
            String text = t.render();
            // 设置发送人邮箱和授权码
            qqMailSender.setUsername(from);
            qqMailSender.setPassword(authWord);
            // 实例化消息对象
            MimeMessage message = qqMailSender.createMimeMessage();
            // 指定 utf-8 防止乱码
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            // 为true 时候 表示文本内容以 html 渲染
            helper.setText(text, true);
            this.qqMailSender.send(message);
            System.out.println("发送邮件成功");
        } catch (MessagingException ex) {
            // 消息发送失败可以做对应的处理
            System.err.println("发送邮件失败" + ex.getMessage());
        }
    }

}

```

**关于模板邮件的说明：**

- 模板引擎最主要的作用是，在对邮件格式有要求的时候，采用拼接字符串不够直观，所以采用模板引擎；

- 这里我们使用的beetl模板引擎，原因是其性能优异，官网是介绍其性能6倍与freemaker,并有完善的文档支持。当然大家也可以换成任何其他的模板引擎（freemarker,thymeleaf）

  一个简单的模板template.html如下：

```html
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
</head>
<body>
    <h1>邮件主题:<span style="color: chartreuse"> ${subject}</span></h1>
    <h4 style="color: blueviolet">${content}</h4>
</body>
</html>
```



#### 2.3 邮件发送的测试

```java
/**
 * @author : heibaiying
 * @description : 发送邮件测试类
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = EmailConfig.class)
public class SendEmail {

    @Autowired
    private SpringMail springMail;

    // 发送方邮箱地址
    private static final String from = "发送方邮箱地址@qq.com";
    // 发送方邮箱地址对应的授权码
    private static final String authWord = "授权码";
    // 接收方邮箱地址
    private static final String to = "接收方邮箱地址@qq.com";

    @Test
    public void sendMessage() {
       
        springMail.sendTextMessage(from, authWord, to, "spring简单邮件", "Hello Spring Email!");
    }


    @Test
    public void sendComplexMessage() {
        Map<String, File> fileMap = new HashMap<>();
        fileMap.put("image1.jpg", new File("D:\\LearningNotes\\picture\\msm相关依赖.png"));
        fileMap.put("image2.jpg", new File("D:\\LearningNotes\\picture\\RabbitMQ模型架构.png"));
        springMail.sendEmailWithAttachments(from, authWord, to, "spring多附件邮件"
                , "Hello Spring Email!", fileMap);
    }

    @Test
    public void sendEmailWithInline() {
        springMail.sendEmailWithInline(from, authWord, to, "spring内嵌资源邮件"
                , "Hello Spring Email!", new File("D:\\LearningNotes\\picture\\RabbitMQ模型架构.png"));
    }

    @Test
    public void sendEmailByTemplate() {
        springMail.sendEmailByTemplate(from, authWord, to,
                "spring模板邮件", "Hello Spring Email!");
    }
}
```
