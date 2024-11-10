Vue.component('user-head', {
    template: `<div>
                    <div class="nx-header">
                        <div style="display: flex;width: 100%; height: 30px; line-height: 30px; background-color: #eee;">
                            <div style="flex: 5; padding: 0 10px; color: orangered">
                                欢迎访问 电影订票购票系统
                            </div>
                          欢迎您：  <div v-if="user.name" style="flex: 1" v-model="user.name">
                                {{user.name}}
                                <a style="margin-left: 30px; color: blue" href="javascript:void(0)" @click="logout">退出</a>
                                <a v-if="isCollect" style="margin-left: 10px" href="collectInfo.html">收藏夹</a>
                            </div>
                        </div>
                    </div>
                    <div style="height: 80px; line-height: 80px;">
                        <div style="margin-left: 20px; position: relative">
                            <img src="img/logo1.png" alt="" style="width: 40px; position: absolute; top: 20px">
                            <b style="margin-left:45px;font-size: 25px; text-shadow: 5px 5px 3px #888888;">电影订票购票系统</b>
                        </div>
                 </div>
                 </div> 
`
});
