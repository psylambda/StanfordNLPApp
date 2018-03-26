# 关键词提取项目

## 主要内容
- [项目功能](#Funciton)
- [项目结构](#Structure)
- [使用方法](#HowToUse)
- [内部实现](#Implementation)


## <h2 id="Funciton">项目功能</h2>
1. 由生语料库生成熟语料库
2. 从熟语料库学习并生成NER模型
3. 从文本中提取文本关键词


## <h2 id="Sturcture">项目结构</h2>
整个项目是在IntelliJ IDEA下开发的Java项目,使用Maven管理包依赖.

-  /lib 存放stanford NER的jar包
-  /src/main/java 存放Java源代码
- /src/main//resources 存放资源文件
     - /Corpus 存放生语料库及标记好的熟语料库
     - /Dict   存放疾病、症状、药品词典
     - /input 存放输入文件
     - /modle 存放训练好的NER模型
     - /output 存放提取好的关键词文件
     - /stanfordNLP 存放stanfordNLP需要的配置文件
     - /keywordsExtraction.properties 存放主配置文件

## <h2 id="HowToUse">使用方法</h2>
修改主配置文件启用相应的功能和配置项，运行team.intelligenthealthcare.keywordsextraction.Main类的main方法即可，

## <h2 id="Implementation">内部实现</h2>
1. 生语料库->熟语料库： 
    -首先将生语料库切片成多个小生语料库
    - 使用stanfordNLP对每个小生语料库分词，并将结果组合
    - 在词典中查找分词后的每一个词或者连续的多个词组成的复合词，如果在词典里，则打上对应词典的标签
    - 生成的文本中每一行首先是一个词，然后是一个tab,最后是一个标签，此即为熟语料库
2. 熟语料库->NER模型:
    - 直接调用edu.stanford.nlp.ie.crf.CRFClassifier的main方法读取配置文件从熟语料库学习出NER模型，此学习算法基于Conditional Random Field模型。[参考资料](https://nlp.stanford.edu/software/CRF-NER.html)
    - 此步骤会消耗大量内存，现在测试大约14万行的熟语料库需要10GB内存才可正常运行，否则会出现java.lang.OutOfMemory错误。解决办法是增大JVM堆内存，如-Xmx10240m -Xms256m. 此外，若物理内存不够，也可考虑将磁盘当作内存分页文件使用，这样程序可正常运行，但会内存页与磁盘页交换时大量读写磁盘，磁盘读写速度可能成为瓶颈。
3. 文本->文本关键词
    - 调用stanfordNLP pipeline, 依次完成分词、分句、POS、NER步骤
    - 此处NER模型即为前一步训练出的模型
