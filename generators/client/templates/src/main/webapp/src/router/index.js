import Vue from 'vue'
import Router from 'vue-router'

// Containers
import Full from '@/containers/Full'

// Views
import Patient from '@/views/Patient'
import Diseases from '@/views/Diseases'

Vue.use(Router)

export default new Router({
  mode: 'hash',
  linkActiveClass: 'open active',
  scrollBehavior: () => ({ y: 0 }),
  routes: [
    {
      path: '/',
      redirect: '/patient',
      name: 'Home',
      component: Full,
      children: [
        {
          path: 'patient',
          name: 'Patient',
          component: Patient
        },
        {
          path: 'diseases',
          name: 'Diseases',
          component: Diseases
        }

      ]
    }
  ]
})
