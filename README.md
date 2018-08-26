# ImageLoader
Android图片加载器
根据<<Android编程权威指南>>中的第25, 26章的知识点, 练习编写了这个图片加载器.  
主要知识点:
1. 使用`AsnycTask`, 从内容提供器中查找到所有的图片及图片目录
2. 使用`HandlerThread`, 获取图片的缩略图, 并不断的发给UIThread.
3. 练习内容解析器, Bitmap缩略图的生成等功能.
4. 使用RecyclerView显示图片缩略图.
