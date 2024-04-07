import { createRouter, createWebHistory } from "vue-router"
import Layout from "../layout/Layout.vue";

const routes = [
  {
    path:'/', //访问根路径，会转向访问Layout界面
    name: 'Layout',
    component: () => import("../layout/Layout.vue"),
    //嵌套路由配置
    children: [
      {
        path: 'home',
        name: '主页',
        component: () => import("../views/Home.vue"),
      },
      {
        path: 'user', 
        name: 'User', 
        component: ()=>import("../views/UserView.vue")
      },  
    
      {
        path: 'robot', name: 'Robot', component: ()=>import("../views/RobotView.vue"),
      },
      {
        path: 'person', name: '个人信息', component: ()=>import("../views/PersonView.vue"),
      },

      {
        path: 'order', name: '订单管理', component: ()=>import("../views/OrderView.vue"),
      }
    ]

  },


  {
    path: '/login',
    name: 'Login',
    component: ()=>import("../views/LoginView.vue")
  },
  {
    path: '/register',
    name: 'Register',
    component: ()=>import("../views/RegisterView.vue")
  }

];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes
});

export default router