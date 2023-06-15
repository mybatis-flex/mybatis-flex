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
.ad .banner a{
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

</style>

<template>
  <Layout>
    <!--docs: https://vitepress.dev/guide/extending-default-theme#layout-slots-->
    <template #doc-footer-before>
      <div class="info custom-block" style="margin-bottom: 14px">
        <!--        <p class="custom-block-title">对这篇文章还有疑问？</p>-->
        <p>对这篇文章还有疑问？ 点击 <a href="https://gitee.com/mybatis-flex/mybatis-flex/issues/new" target="_blank">[这里]</a> 发起提问。
        </p>
      </div>
    </template>

    <template #aside-outline-after>
      <div class="ad">
        <div class="title">
          <a href="/zh/becoming-a-sponsor.html">成为赞助者</a>
        </div>

        <div class="content">
          <div class="banner">
            <a href="http://www.codeformat.cn" target="_blank"><img src="/assets/images/ad/logo-code-format.png"></a>
          </div>
          <div class="banner">
            虚以待位
          </div>
          <div class="banner">
            虚以待位
          </div>
          <div class="banner">
            虚以待位
          </div>
          <div class="banner">
            虚以待位
          </div>
          <div class="banner">
            虚以待位
          </div>
          <div class="banner">
            虚以待位
          </div>
          <div class="banner">
            虚以待位
          </div>
        </div>
        <div class="banner-bottom">
          虚以待位
        </div>
      </div>
    </template>
  </Layout>
</template>