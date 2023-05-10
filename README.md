###ETL和taptap爬虫 
> 线下启动命令:nohup java -jar -Xmx512m ./WebAppcenter.jar  --server.port=7777  --spring.profiles.active=dev  > /dev/null 2>&1 & 

> 线上启动命令:nohup java -jar -Xmx512m ./WebAppcenter.jar  --server.port=7777  --spring.profiles.active=prd  > /dev/null 2>&1 &


国内taptap爬虫环境安装:
1、sudo yum install dnf
2、wget https://dl.google.com/linux/direct/google-chrome-stable_current_x86_64.rpm
3、sudo dnf install -y google-chrome-stable_current_x86_64.rpm

4、查看chrome版本:google-chrome --version

5、下载chrome驱动: wget https://chromedriver.storage.googleapis.com/105.0.5195.52/chromedriver_linux64.zip
(注意chrome driver版本需要和chrome版本匹配,下载地址:https://chromedriver.chromium.org/downloads 或者  https://chromedriver.storage.googleapis.com/index.html)

6、解压驱动包: unzip chromedriver_linux64.zip (也可以通过xftp上传 然后chmod 777)

7、设置 webdriver.chrome.driver 的路径为驱动路径

8、设置系统语言为(zh-CN) 
检验:echo $LANG

9、modheader.crx是chrome浏览器的插件,可以修改请求头

设置环境变量:  --spider=taptap
启动脚本
eval $(ps --no-heading -aux |grep "WebAppcenterETL.jar" | grep -v "grep"|awk '{print "kill -9 " $2}')
nohup java -jar WebAppcenterETL.jar  --spring.profiles.active=prd --spider=taptap   >/dev/null  2>&1 &