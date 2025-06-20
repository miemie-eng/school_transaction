import { createRouter, createWebHistory } from 'vue-router'


const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'login',
      component: () => import('../views/Login.vue'),
    },
	{
		path: '/Home',
		name: 'home',
		component: () => import('../views/Home.vue'),
		children:[
				  {
					  path: '/shop',
					  name: 'shop',
					  component: () => import('../views/shop.vue'),
				  },
				  {
				  			  path: '/myProduct',
				  			  name: 'myProduct',
				  			  component: () => import('../views/myProduct.vue'),
				  },
				 
				  
				  {
				  			  path: '/QA',
				  			  name: 'QA',
				  			  component: () => import('../views/QA.vue'),
				  },
				  {
				  			  path: '/talk',
				  			  name: 'talk',
				  			  component: () => import('../views/talk.vue'),
				  },
				  {
				  			  path: '/me',
				  			  name: 'me',
				  			  component: () => import('../views/me.vue'),
				  },
		]
	},
    {
      path: '/about',
      name: 'about',
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/AboutView.vue'),
    },
  ],
})

export default router
