XDOC (mini)
==============================

![XDOC](http://static.oschina.net/uploads/space/2015/0825/114225_75kc_193624.png "")

简介
------------------------------

XDOC(mini)是XDOC的精简版，项目的主要目标是提供一个基于XML的文档描述语言，
使得动态网页语言（JSP、ASP、PHP等）和模板引擎可以高效的生成PDF等版式文档。

特点
------------------------------

* 纯Java Web应用，直接部署，无需配置
* 基于HTTP服务
* 与HTML完美融合，自动渲染，适合各种动态网页语言
* 丰富的排版标签：段落、文字、图形、图片、表格等
* 丰富的排版属性

示例
------------------------------

``` HTML
<!DOCTYPE html>
<html lang="zh-cn" style="height:100%">
  <head>
    <meta charset="utf-8" />
    <title>XDOC</title>
    <script type="text/javascript" src="http://localhost:8080/xdoc/xdoc.js"></script>
  </head>
  <body style="margin:0px;overflow:hidden;height:100%">
    <script type="text/xdoc" _format="pdf" style="width:100%;height:100%">
    <xdoc>
      <body>
        <para heading="1" lineSpacing="28">
          <text valign="center" fontName="标宋" fontSize="29">欢迎使用XDOC!</text>
        </para>
      </body>
    </xdoc>
    </script>
  </body>
</html>
```

安装包
------------------------------

http://git.oschina.net/xdoc/xdoc/blob/master/xdoc.war

规范
------------------------------

http://git.oschina.net/xdoc/xdoc/blob/master/xdoc.pdf

技术支持
------------------------------

http://www.xdocin.com