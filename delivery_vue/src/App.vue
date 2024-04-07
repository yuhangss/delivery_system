<!-- <template>
  <nav>
    <router-link to="/">Home</router-link> |
    <router-link to="/about">About</router-link>
  </nav>
  <router-view/>
</template>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}

nav {
  padding: 30px;
}

nav a {
  font-weight: bold;
  color: #2c3e50;
}

nav a.router-link-exact-active {
  color: #42b983;
}
</style> -->

<template>
  <div>
    <el-config-provider :locale="locale">
      <router-view/>
    </el-config-provider>
  </div>
</template>

<script>
import locale from 'element-plus/lib/locale/lang/zh-cn';

export default {
  name: 'App',
  components: {

  },
  setup() {
    return {locale}
  },
}
</script>

<style>

</style>

