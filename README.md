#### 安卓悬浮按钮,依附于decorView不需要各种权限!你懂的!同时支持拖拽，长按事件自动靠边 唯一缺陷在于依附当前activity因此在activity切换的时候会覆盖，但是相比于各种蛋疼的权限 我觉得这点儿是可以接受的

#### 来来来 先瞅瞅效果
<a><img src="art/art1.gif" width="60%"/></a>
<a><img src="art/art2.gif" width="60%"/></a>

#### 其他 代码中都有勒  自己看吧注意用单例管理的 切记在onresume中调用attach ~~~
