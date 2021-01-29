<template>
  <ion-page>
    <ion-tabs>
      <ion-tab-bar :class="'AllTabs'" >
        <ion-tab-button :class="' onsConverts'" v-on:click="Tabs()" tab="tab1" href="/tabs/tab1">
          <svg xmlns='http://www.w3.org/2000/svg' class='TabsClass-Active ionicon' viewBox='0 0 512 512'><title>Rocket</title><path d='M461.81 53.81a4.4 4.4 0 00-3.3-3.39c-54.38-13.3-180 34.09-248.13 102.17a294.9 294.9 0 00-33.09 39.08c-21-1.9-42-.3-59.88 7.5-50.49 22.2-65.18 80.18-69.28 105.07a9 9 0 009.8 10.4l81.07-8.9a180.29 180.29 0 001.1 18.3 18.15 18.15 0 005.3 11.09l31.39 31.39a18.15 18.15 0 0011.1 5.3 179.91 179.91 0 0018.19 1.1l-8.89 81a9 9 0 0010.39 9.79c24.9-4 83-18.69 105.07-69.17 7.8-17.9 9.4-38.79 7.6-59.69a293.91 293.91 0 0039.19-33.09c68.38-68 115.47-190.86 102.37-247.95zM298.66 213.67a42.7 42.7 0 1160.38 0 42.65 42.65 0 01-60.38 0z' fill='none' stroke='currentColor' stroke-linecap='round' stroke-linejoin='round' stroke-width='32'/><path d='M109.64 352a45.06 45.06 0 00-26.35 12.84C65.67 382.52 64 448 64 448s65.52-1.67 83.15-19.31A44.73 44.73 0 00160 402.32' fill='none' stroke='currentColor' stroke-linecap='round' stroke-linejoin='round' stroke-width='32'/></svg>
          <ion-label>Conversion</ion-label>
        </ion-tab-button>
        
        <ion-tab-button :class="'onsAbout'" v-on:click="Tabss()"  tab="tab3" href="/tabs/tab3">
          <svg xmlns='http://www.w3.org/2000/svg' class='ionicon' viewBox='0 0 512 512'><title>Rose</title><path d='M416 128c-18.9 4.25-36.8 8.94-53.7 13.95-40.5 12-75.5 27.15-105.4 41.65-19.3 9.37-26.2 13.51-51.5 28.23-58.4 33.69-93.4 77.4-93.4 142.81C112 428.55 167.6 480 256 480s144-55.81 144-129.72S339 225.24 416 128z' fill='none' stroke='currentColor' stroke-linecap='round' stroke-linejoin='round' stroke-width='32'/><path d='M264 180.19c-19.69-27-38.2-38.69-52.7-46.59C162.6 107.1 96 96 96 96c41.5 43.7 37.2 90.1 32 128 0 0-3.87 32.88 1.91 58.41' fill='none' stroke='currentColor' stroke-linecap='round' stroke-linejoin='round' stroke-width='32'/><path d='M372 139.15C356.55 102.6 336 64 336 64s-63.32 0-135.69 64M253.48 87.57C221.25 45.81 176 32 176 32c-15.3 20.8-28.79 51.58-34.87 74.17' fill='none' stroke='currentColor' stroke-linecap='round' stroke-linejoin='round' stroke-width='32'/></svg>
          <ion-label>A propos</ion-label>
        </ion-tab-button>
      </ion-tab-bar>
    </ion-tabs>
  </ion-page>
</template>

<script lang="ts">
import { IonTabBar, IonTabButton, IonTabs, IonLabel, IonPage } from '@ionic/vue';
import { ellipse, square, triangle, rocketOutline, roseOutline } from 'ionicons/icons';


export default {
  name: 'Tabs',
  components: { IonLabel, IonTabs, IonTabBar, IonTabButton, IonPage },

  

  setup() {

    const Tabs = () => {
      document.querySelector('.onsConverts svg').classList.add('TabsClass-Active')
      document.querySelector('.onsAbout svg').classList.remove('TabsClass-Active')
    }

    const Tabss = () => {
      document.querySelector('.onsAbout svg').classList.add('TabsClass-Active')
      document.querySelector('.onsConverts svg').classList.remove('TabsClass-Active')
    }


    return {
      ellipse, 
      square, 
      triangle,
      rocketOutline,
      roseOutline,

      Tabs,
      Tabss
    }
  }
}
</script>

<style >

.AllTabs svg{
  width: 25px;
  height: 25px;
  transition: all 0.5s;
}

.TabsClass-Active{
  width: 30px !important;
  height: 30px !important;
  transition: all 0.5s;
}

.tab-selected ion-label{
  font-weight: 700 !important;
}
  
</style>