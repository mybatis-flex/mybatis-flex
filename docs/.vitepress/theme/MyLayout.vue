<!--
  -  Copyright (c) 2022-2025, Mybatis-Flex (fuhai999@gmail.com).
  -  <p>
  -  Licensed under the Apache License, Version 2.0 (the "License");
  -  you may not use this file except in compliance with the License.
  -  You may obtain a copy of the License at
  -  <p>
  -  http://www.apache.org/licenses/LICENSE-2.0
  -  <p>
  -  Unless required by applicable law or agreed to in writing, software
  -  distributed under the License is distributed on an "AS IS" BASIS,
  -  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  -  See the License for the specific language governing permissions and
  -  limitations under the License.
  -->

<!--.vitepress/theme/MyLayout.vue-->

<script setup>
import DefaultTheme from 'vitepress/theme'

const {Layout} = DefaultTheme
</script>

<style>
.ad {
    margin-top: 20px;
}

.ad > .title {
    text-align: center;
}

.ad > .title a {
    color: #82AAFF;
    font-size: 14px;
}

.ad > .content {
    display: flex;
    flex-wrap: wrap;
    align-items: flex-start;
}

.ad .banner {
    width: 105px;
    margin: 1px;
    height: 50px;
    font-size: 14px;
    line-height: 50px;
    background: #f7f7f7;
    border-radius: 3px;
    text-align: center;
    color: #666;
}

.ad .banner a {
    display: inline-block;
    vertical-align: middle;
}

.banner-bottom {
    width: 212px;
    margin: 1px;
    height: 50px;
    font-size: 14px;
    line-height: 50px;
    background: #f7f7f7;
    text-align: center;
    color: #666;
}

.banner-home {
    display: flex;
    align-items: center;
    margin: 60px auto;
    width: 50%;
    justify-content: center;
}

.banner-home img {
    max-width: 100%;
    border-radius: 3px;
}

@media screen and (max-width: 800px) {
    .banner-home {
        width: 90%;
        margin: 30px auto;
    }
}


</style>

<template>
    <Layout>

        <!--docs: https://vitepress.dev/guide/extending-default-theme#layout-slots-->
        <template #doc-before>
            <div style="margin-bottom: 30px">
                <a href="https://gitee.com/activity/2025opensource?ident=INV9BE" target="_blank">
                    <img src="/assets/images/ad/aiflowy-banner.jpg">
                </a>
            </div>
        </template>

        <template #home-features-after>
            <div class="banner-home">
                <a href="https://gitee.com/activity/2025opensource?ident=INV9BE" target="_blank">
                    <img src="/assets/images/ad/aiflowy-banner.jpg">
                </a>
            </div>
        </template>


        <!--        <template #home-features-after>-->
        <!--            <div style="display: flex; justify-content: center;margin-top: 60px">-->
        <!--                <a href="https://mp.weixin.qq.com/s/1pTQ9m3C4ebfSQdyWUAyaQ" target="_blank" style="max-width: 90%">-->
        <!--                    <img src="/assets/images/ad/aiadmin.gif" style="width: 650px;">-->
        <!--                </a>-->
        <!--            </div>-->
        <!--        </template>-->

        <template #doc-footer-before>
            <div class="info custom-block" style="margin-bottom: 14px">
                <!--        <p class="custom-block-title">对这篇文章还有疑问？</p>-->
                <p>对这篇文章还有疑问？ 点击 <a href="https://gitee.com/mybatis-flex/mybatis-flex/issues/new"
                                               target="_blank">[这里]</a> 发起提问。
                </p>
            </div>
        </template>

        <template #aside-outline-after>
            <div class="ad">
                <div class="title">
                    <a href="/zh/becoming-a-sponsor.html">成为赞助者</a>
                </div>

                <div class="content">
<!--                    <div class="banner">-->
<!--                        <a href="https://dromara.gitee.io/fast-request/" target="_blank"><img-->
<!--                            src="/assets/images/ad/fast-request-20230828.svg" style="width: 120px;height: 50px"></a>-->
<!--                    </div>-->
<!--                    <div class="banner">-->
<!--                        <a href="https://www.5iot.com/source?key=flex" target="_blank"><img-->
<!--                            src="/assets/images/ad/iotlink_20240802.png"></a>-->
<!--                    </div>-->
                    <div class="banner">
                        <a href="http://www.jpress.cn" target="_blank" style="height:50px;
            background: rgb(247 247 247);width: 105px;color: #000;font-size: 18px">
                            信创 CMS
                        </a>
                    </div>
<!--                    <div class="banner">-->
<!--                        <a href="http://www.haorentech.com" target="_blank"><img-->
<!--                            src="/assets/images/ad/haorentech_20230901.png" style="width: 105px;height: 50px"></a>-->
<!--                    </div>-->
                    <div class="banner">
                        <a href="http://www.codeformat.cn" target="_blank"><img src="/assets/images/ad/code-format.png"></a>
                    </div>
<!--                    <div class="banner">-->
<!--                        <a href="https://eiam.topiam.cn" target="_blank"><img-->
<!--                            src="/assets/images/ad/topiam_20230909.png" style="width: 105px;height: 50px"></a>-->
<!--                    </div>-->
                    <div class="banner">
                        虚位以待
                    </div>
                    <div class="banner">
                        虚位以待
                    </div>
                </div>
                <div class="banner-bottom">
                    <a href="https://www.orangeforms.com" target="_blank"><img
                        src="/assets/images/ad/cd_20250620.jpg" data-expired="20250620"></a>
                </div>
<!--                <div class="banner-bottom">-->
<!--                    <a href="https://www.diboot.com/?from=mf" target="_blank"><img-->
<!--                        src="https://www.diboot.com/diboot_mf.png" data-expired="20250116"></a>-->
<!--                </div>-->
            </div>
        </template>
    </Layout>
</template>
