<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <ion-title>8to10</ion-title>
      </ion-toolbar>
    </ion-header>
    <ion-content :fullscreen="true">
      <ion-header collapse="condense">
        <ion-toolbar>
          <ion-title size="large">{{ title }}</ion-title>

        </ion-toolbar>
      </ion-header>


      <ion-list>
        <ion-item>
          <ion-label> {{ numberOfContacts }} Numéro(s) de téléphone trouvé(s)</ion-label>
        </ion-item>
        <ion-item>
          <ion-label>{{ progressionStatus ? progressionStatus : "Press here to start conversion" }}</ion-label>
        </ion-item>
        <ion-item>
          <ion-label>{{ progressionStatusValue }}%</ion-label>
        </ion-item>
      </ion-list>

      <div class="btn-wrapper">
        <ion-button color="primary" v-on:click="updateAllContact">Passez à dix chiffres</ion-button>
      </div>
      <div class="btn-wrapper">
        <ion-button color="danger" v-on:click="restoreAllContact">Revenir à huit chiffres</ion-button>
      </div>

    </ion-content>
  </ion-page>
</template>

<script>
import {IonPage, IonHeader, IonToolbar, IonTitle, IonContent} from '@ionic/vue';
import {Plugins} from '@capacitor/core';


const {ContactsCustomPlugin} = Plugins;

export default {

  name: "Conversion",
  components: {IonHeader, IonToolbar, IonTitle, IonContent, IonPage},


  data() {
    return {
      numberOfContacts: 0,

      progressionStatus: "",
      progressionStatusValue: 0,
      progressionCurrentLimit: 0,
      progressionEndMessage: "",
      progressionHandler: null,

    }
  },

  mounted() {
    this.countPhoneNumbers();
  },

  methods: {
    countPhoneNumbers() {
      ContactsCustomPlugin.countPhoneNumbers().then(result => {
        this.numberOfContacts = result["count"];
      })
    },

    async updateAllContact() {
      this.resetProgression();

      let contactsHasBeenUpdated = false;
      this.progressionStatus = "Préparation ..."
      this.fakeProgressionStatusValue(50);
      await ContactsCustomPlugin.updateUserContacts().then(result => {
          this.progressionStatus = "Mis à jour des numéros de téléphone en cours ...";
          contactsHasBeenUpdated = result["updated"];
      });

      if (contactsHasBeenUpdated) {
          this.fakeProgressionStatusValue(100, "Les numéros de téléphone ont été parfaitement mis à jour!");

      } else {
          this.progressionStatus = "La mise à jour a échoué !";
      }
    },


    resetProgression() {
      this.progressionStatusValue = 0;
    },

    fakeProgressionStatusValue(limit, message) {
      this.progressionCurrentLimit = limit;
      this.progressionEndMessage = message;
      this.progressionHandler = setInterval(this.incrementProgressionStatusValue, 100);
    },

    incrementProgressionStatusValue() {
      if (this.progressionStatusValue < this.progressionCurrentLimit)
        this.progressionStatusValue += 1;
      else {
        clearInterval(this.progressionHandler);
        this.progressionStatus = this.progressionEndMessage;
      }

    },

    async restoreAllContact() {
      this.resetProgression();
      let hasBeenRestored = false;
      this.fakeProgressionStatusValue(50)
      await ContactsCustomPlugin.undoUpdateUserContacts().then(result => {
          this.progressionStatus = "Restauration en cours ...";
          hasBeenRestored = result["restored"];
      });

      if(hasBeenRestored)
          this.fakeProgressionStatusValue(100, "Les numéros de téléphone ont été parfaitement mis à jour!");
      else
        console.log("La restauration a échoué !")

    }

  }
}
</script>

<style scoped>
.btn-wrapper {
  margin: 8px 25px;
  text-align: center; /* center inline and inline-block element inside */
}
</style>

/*
* ionic build
* npx cap open android | ouvrir android studio
* npx cap init [appName] [appId] | permet de definir le nom et l'identifiant de l'application
* npx cap sync to update custom plugins
* npx cap copy | Synchroniser l'application avec capicitor
*/