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
          <ion-label> {{ numberOfContacts }} Phone numbers found on this phone</ion-label>
        </ion-item>
        <ion-item>
          <ion-label>{{ progressionStatus ? progressionStatus: "Press here to start conversion" }}</ion-label>
        </ion-item>
        <ion-item>
          <ion-label>{{ progressionStatusValue }}%</ion-label>
        </ion-item>
      </ion-list>

      <div class="btn-wrapper">
        <ion-button color="primary" v-on:click="updateAllContact">Convert Phone Number</ion-button>
      </div>
      <div class="btn-wrapper" v-show="restorationPointExists">
        <ion-button color="danger" v-on:click="restoreAllContact">Restore Phone Number</ion-button>
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

      restorationPointExists: false,
    }
  },

  mounted() {
    this.countPhoneNumbers();
    this.checkIfRestorationPointExists();
  },

  methods: {
    countPhoneNumbers() {
      ContactsCustomPlugin.countPhoneNumbers().then(result => {
        this.numberOfContacts = result["count"];
      })
    },
    
    checkIfRestorationPointExists() {
        
        ContactsCustomPlugin.restorationPointExists().then(result => {
            this.restorationPointExists = result["exists"];
        });
    },

    async updateAllContact() {
      this.resetProgression();
      this.progressionStatus = "Creation of restoration point ...";

      let contactsHasBeenSaved = false;
      await ContactsCustomPlugin.saveUserContactsAsVcf().then(result => {
        contactsHasBeenSaved = result["saved"];
      });


      if (contactsHasBeenSaved) {
        this.fakeProgressionStatusValue(30, "Restoration point created!");
        let contactsHasBeenUpdated = false;
        await ContactsCustomPlugin.updateUserContacts().then(result => {
          this.progressionStatus = "Updating contacts ...";
          contactsHasBeenUpdated = result["updated"];
        });

        if (contactsHasBeenUpdated) {
          this.fakeProgressionStatusValue(100, "Contacts has been updated successfully!");

        } else {
          this.progressionStatus = "Contacts Update failed !";
        }

      } else {
        this.progressionStatus = "Restoration point failed!";
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
        if(this.progressionStatusValue < this.progressionCurrentLimit)
          this.progressionStatusValue += 1;
        else {
          clearInterval(this.progressionHandler);
          this.progressionStatus = this.progressionEndMessage;
        }


    },

    async restoreAllContact() {
        
        await ContactsCustomPlugin.openRestorationPointFile().then(result => {

            if(result["opened"]) {

                ContactsCustomPlugin.deleteUserContacts().then(result => {
                      if(result["deleted"]) {
                          console.log("all contacts has been deleted");
                      } else {
                          console.log("deletion failed");
                      }
                });
            } else {
                console.log("has not been opened");
            }
        });

        
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