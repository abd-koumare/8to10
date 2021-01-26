<template>
  <ion-page>
    <ion-tabs>
      <ion-tab-bar >
        <ion-tab-button tab="tab1" href="/tabs/tab1">
          <ion-icon :icon="rocketOutline" />
          <ion-label>Conversion</ion-label>
        </ion-tab-button>
        
        <ion-tab-button tab="tab3" href="/tabs/tab3">
          <ion-icon :icon="roseOutline" />
          <ion-label>A propos</ion-label>
        </ion-tab-button>
      </ion-tab-bar>
    </ion-tabs>
  </ion-page>
</template>

<script lang="ts">
import { IonTabBar, IonTabButton, IonTabs, IonLabel, IonIcon, IonPage } from '@ionic/vue';
import { ellipse, square, triangle, rocketOutline, roseOutline } from 'ionicons/icons';

export default {
  name: 'Tabs',
  components: { IonLabel, IonTabs, IonTabBar, IonTabButton, IonIcon, IonPage },
  setup() {
    return {
      ellipse, 
      square, 
      triangle,
      rocketOutline,
      roseOutline
    }
  }
}
</script>