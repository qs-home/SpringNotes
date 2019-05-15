# spring +druid+ mybatis（xml配置方式）

## 目录<br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#1创建maven工程除了Spring基本依赖外还需要导入mybatis和druid的相关依赖">1、创建maven工程，除了Spring基本依赖外，还需要导入mybatis和druid的相关依赖</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#2在webxml-中配置spring前端控制器druid监控台servlet和filter">2、在web.xml 中配置spring前端控制器、druid监控台servlet和filter</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#3在resources文件夹下新建数据库配置文件jdbcproperties">3、在resources文件夹下新建数据库配置文件jdbc.properties</a><br/
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#4在resources文件夹下创建springApplicationxml-配置文件和druidxml配置文件">4、在resources文件夹下创建springApplication.xml 配置文件和druid.xml配置文件 </a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#5新建mybtais-配置文件按需要进行额外配置更多settings配置项可以参考[官方文档]http//wwwmybatisorg/mybatis-3/zh/configurationhtml">5、新建mybtais 配置文件，按需要进行额外配置，更多settings配置项可以参考[官方文档](http://www.mybatis.org/mybatis-3/zh/configuration.html)</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#6新建查询接口及其对应的mapper文件">6、新建查询接口及其对应的mapper文件</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#7新建测试controller进行测试">7、新建测试controller进行测试</a><br/>
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="#8druid-监控页面访问地址http//localhost8080/druid/indexhtml">8、druid 监控页面访问地址http://localhost:8080/druid/index.html</a><br/>
## 正文<br/>

### 项目目录结构

<div align="center"> <img src="https://github.com/qshomewy/SpringNotes/blob/master/pictures/spring-druid-mybatis.png"/> </div>

#### 1、创建maven工程，除了Spring基本依赖外，还需要导入mybatis和druid的相关依赖

```xml
<!--jdbc 相关依赖包-->
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-jdbc</artifactId>
    <version>${spring-base-version}</version>
</dependency>
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.13</version>
</dependency>
<dependency>
    <groupId>com.oracle</groupId>
    <artifactId>ojdbc6</artifactId>
    <version>11.2.0.3.0</version>
</dependency>
<!--mybatis 依赖包-->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>1.3.2</version>
</dependency>
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.4.6</version>
</dependency>
<!--druid 依赖-->
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.1.12</version>
</dependency>
```

#### 2、在web.xml 中配置spring前端控制器、druid监控台servlet和filter

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
		 http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
         version="3.1">

    <!--配置spring前端控制器-->
    <servlet>
        <servlet-name>springMvc</servlet-name>
        <servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>contextConfigLocation</param-name>
            <param-value>classpath:springApplication.xml</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>springMvc</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

    <!-- 配置 Druid 监控信息显示页面 访问地址 <a href="http://localhost:8080/druid/index.html"> -->
    <servlet>
        <servlet-name>DruidStatView</servlet-name>
        <servlet-class>com.alibaba.druid.support.http.StatViewServlet</servlet-class>
        <init-param>
            <!-- 允许清空统计数据 -->
            <param-name>resetEnable</param-name>
            <param-value>true</param-value>
        </init-param>
        <init-param>
            <!-- 用户名 -->
            <param-name>loginUsername</param-name>
            <param-value>druid</param-value>
        </init-param>
        <init-param>
            <!-- 密码 -->
            <param-name>loginPassword</param-name>
            <param-value>druid</param-value>
        </init-param>
    </servlet>
    <servlet-mapping>
        <servlet-name>DruidStatView</servlet-name>
        <url-pattern>/druid/*</url-pattern>
    </servlet-mapping>

    <!--配置WebStatFilter用于采集web-jdbc关联监控的数据-->
    <filter>
        <filter-name>DruidWebStatFilter</filter-name>
        <filter-class>com.alibaba.druid.support.http.WebStatFilter</filter-class>
        <init-param>
            <param-name>exclusions</param-name>
            <param-value>*.js,*.gif,*.jpg,*.png,*.css,*.ico,/druid/*</param-value>
        </init-param>
    </filter>
    <filter-mapping>
        <filter-name>DruidWebStatFilter</filter-name>
        <url-pattern>/*</url-pattern>
    </filter-mapping>


</web-app>
```

#### 3、在resources文件夹下新建数据库配置文件jdbc.properties

```properties
# mysql 数据库配置
mysql.url=jdbc:mysql://localhost:3306/mysql
mysql.username=root
mysql.password=root

# oracle 数据库配置
oracle.url=jdbc:oracle:thin:@//IP地址:端口号/数据库实例名
oracle.username=用户名
oracle.password=密码
```

#### 4、在resources文件夹下创建springApplication.xml 配置文件和druid.xml配置文件 
[【Spring框架】<mvc:default-servlet-handler/>的作用](https://www.cnblogs.com/dflmg/p/6393416.html)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context" xmlns:tx="http://www.springframework.org/schema/tx"
       xmlns:mvc="http://www.springframework.org/schema/mvc"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.1.xsd http://www.springframework.org/schema/tx http://www.springframework.org/schema/tx/spring-tx.xsd http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd">

    <!-- 开启注解包扫描-->
    <context:component-scan base-package="com.qs.*"/>

    <!--使用默认的Servlet来响应静态文件 -->
    <mvc:default-servlet-handler/>

    <!-- 开启注解驱动-->
    <mvc:annotation-driven/>

    <!--引入druid.xml配置 由于druid 的可配置项比较多，所以可以单独拆分为一个配置文件-->
    <import resource="druid.xml"/>

    <!--配置 mybatis 会话工厂 -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <property name="dataSource" ref="dataSource"/>
        <!--指定mapper文件所在的位置-->
        <property name="mapperLocations" value="classpath*:/mappers/**/*.xml"/>
        <property name="configLocation" value="classpath:mybatisConfig.xml"/>
    </bean>

    <!--扫描注册接口 -->
    <!--作用:从接口的基础包开始递归搜索，并将它们注册为 MapperFactoryBean(只有至少一种方法的接口才会被注册;, 具体类将被忽略)-->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!--指定会话工厂 -->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory"/>
        <!-- 指定mybatis接口所在的包 -->
        <property name="basePackage" value="com.qs.dao"/>
    </bean>

    <!--定义事务管理器-->
    <bean id="transactionManager"
          class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!-- 开启事务注解@Transactional支持 -->
    <tx:annotation-driven/>

</beans>
```
[DruidDataSource配置属性列表](https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE%E5%B1%9E%E6%80%A7%E5%88%97%E8%A1%A8)
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.1.xsd">


    <!--指定配置文件的位置-->
    <context:property-placeholder location="classpath:jdbc.properties"/>

    <!--配置druid数据源 关于更多的配置项 可以参考官方文档 <a src="https://github.com/alibaba/druid/wiki/DruidDataSource%E9%85%8D%E7%BD%AE%E5%B1%9E%E6%80%A7%E5%88%97%E8%A1%A8" > -->
    <bean id="dataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">
        <!-- 基本属性 url、user、password -->
        <property name="url" value="${mysql.url}"/>
        <property name="username" value="${mysql.username}"/>
        <property name="password" value="${mysql.password}"/>

        <!-- 配置初始化大小、最小、最大连连接数量 -->
        <property name="initialSize" value="5"/>
        <property name="minIdle" value="10"/>
        <property name="maxActive" value="20"/>

        <!-- 配置获取连接等待超时的时间 -->
        <property name="maxWait" value="60000"/>

        <!-- 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒 -->
        <property name="timeBetweenEvictionRunsMillis" value="2000"/>

        <!-- 配置一个连接在池中最小生存的时间，单位是毫秒 -->
        <property name="minEvictableIdleTimeMillis" value="600000"/>
        <!-- 配置一个连接在池中最大生存的时间，单位是毫秒 -->
        <property name="maxEvictableIdleTimeMillis" value="900000"/>

        <!--validationQuery 用来检测连接是否有效的sql，要求是一个查询语句，常用select 'x'。
            但是在 oracle 数据库下需要写成 select 'x' from dual 不然实例化数据源的时候就会失败,
            这是由于oracle 和 mysql 语法间的差异造成的-->
        <property name="validationQuery" value="select 'x'"/>
        <!--建议配置为true，不影响性能，并且保证安全性。申请连接的时候检测，
        如果空闲时间大于timeBetweenEvictionRunsMillis，执行validationQuery检测连接是否有效。-->
        <property name="testWhileIdle" value="true"/>
        <!--申请连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。-->
        <property name="testOnBorrow" value="false"/>
        <!--归还连接时执行validationQuery检测连接是否有效，做了这个配置会降低性能。-->
        <property name="testOnReturn" value="false"/>

        <!--连接池中的minIdle数量以内的连接，空闲时间超过minEvictableIdleTimeMillis，则会执行keepAlive操作。-->
        <property name="keepAlive" value="true"/>
        <property name="phyMaxUseCount" value="100000"/>

        <!-- 配置监控统计拦截的filters Druid连接池的监控信息主要是通过StatFilter 采集的，
        采集的信息非常全面，包括SQL执行、并发、慢查、执行时间区间分布等-->
        <property name="filters" value="stat"/>
    </bean>

</beans>
```

#### 5、新建mybtais 配置文件，按需要进行额外配置，更多settings配置项可以参考[官方文档](http://www.mybatis.org/mybatis-3/zh/configuration.html)

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">

<!-- mybatis 配置文件 -->
<configuration>
    <settings>
        <!-- 开启驼峰命名 -->
        <setting name="mapUnderscoreToCamelCase" value="true"/>
        <!-- 打印查询sql -->
        <setting name="logImpl" value="STDOUT_LOGGING"/>
    </settings>

</configuration>

<!--更多settings配置项可以参考官方文档: <a href="http://www.mybatis.org/mybatis-3/zh/configuration.html"/>-->

```

#### 6、新建查询接口及其对应的mapper文件

```java
public interface MysqlDao {

    List<Relation> get();
}
```

```xml
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.qs.dao.MysqlDao">

    <select id="queryById" resultType="com.qs.bean.Relation">
        SELECT help_keyword_id AS id,name
        FROM HELP_KEYWORD
        WHERE HELP_KEYWORD_ID = #{id}
    </select>

</mapper>
```

```java
public interface OracleDao {

    List<Flow> queryById(long id);
}
```

```xml
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">

<mapper namespace="com.qs.dao.OracleDao">

    <select id="queryById" resultType="com.qs.bean.Flow">
        select * from APEX_030200.WWV_FLOW_CALS where ID = #{id}
    </select>

</mapper>
```

#### 7、新建测试controller进行测试

```java
@RestController
public class MysqlController {

    @Autowired
    private MysqlDao mysqlDao;

    @GetMapping("relation/{id}")
    public String get(@PathVariable(name = "id") String id) {
        return mysqlDao.queryById(id).get(0).toString();
    }
}
```

```java
@RestController
public class OracleController {

    @Autowired
    private OracleDao oracleDao;

    @GetMapping("flow/{id}")
    public String get(@PathVariable(name = "id") Long id) {
        return oracleDao.queryById(id).get(0).toString();
    }
}
```

#### 8、druid 监控页面访问地址http://localhost:8080/druid/index.html

![druid控制台](https://github.com/qshomewy/SpringNotes/blob/master/pictures/druid%E6%8E%A7%E5%88%B6%E5%8F%B0.png)
