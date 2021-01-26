<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <div class="OB-toolbar">
          <img class="OB-toolbar-img" src="/assets/icon/Logo-8to10.svg" />
          <div v-if="restorationPointExists" v-on:click="RestoreIfRestorationPointExists" class="OB-toolbar-img-restore">
            <img class="" src="/assets/icon/refresh-circle.svg" />
          </div>
          <div v-else v-on:click="NotRestorationPointExists" class="OB-toolbar-img-restore">
            <img class="" src="/assets/icon/refresh-circle.svg" />
          </div>
        </div>
      </ion-toolbar>
    </ion-header>
    <ion-content :fullscreen="true">
      <div class="OB-Container">
        

        <div class="OB-progressionEvolution">
          <div class="progressionEvolution-shadow">
            <div class="progressionEvolution-ligth">
              <span>{{ progressionStatusValue }}%</span>
            </div>
          </div>
        </div>

        <div class="OB-progressionStatus">
          <span> - {{
            progressionStatus
              ? progressionStatus
              : "En attente pour commencer"
          }} - </span>
        </div>

        <div class="OB-numberOfContacts">
          <span>{{ numberOfContacts }} Contacts</span>
        </div>

        <div class="OB-btn" >
          <div v-on:click="updateAllContact" class="OB-btn-Coversion">
            <img src="/assets/icon/rocket-outline.svg" alt="" />
            <span> Convertir </span>
          </div>
        </div>
      </div>

  
    </ion-content>
  </ion-page>
</template>

<script>
import { IonPage, IonHeader, IonToolbar, IonContent, alertController } from "@ionic/vue";
import { Plugins } from "@capacitor/core";
import { refreshCircle } from "ionicons/icons";

const { ContactsCustomPlugin } = Plugins;

export default {
  name: "Conversion",
  components: { IonHeader, IonToolbar, IonContent, IonPage },

  data() {
    return {
      refreshCircle,

      numberOfContacts: 0,

      progressionStatus: "",
      progressionStatusValue: 0,
      progressionCurrentLimit: 0,
      progressionEndMessage: "",
      progressionHandler: null,

      restorationPointExists: false,
    };
  },

  mounted() {
    this.countPhoneNumbers();
    this.checkIfRestorationPointExists();
  },

  methods: {
    countPhoneNumbers() {
      ContactsCustomPlugin.countPhoneNumbers().then((result) => {
        this.numberOfContacts = result["count"];
      });
    },

    checkIfRestorationPointExists() {
      ContactsCustomPlugin.restorationPointExists().then((result) => {
        this.restorationPointExists = result["exists"];
      });
    },

    async updateAllContact() {
      this.resetProgression();
      this.progressionStatus = "Creation of restoration point...";

      let contactsHasBeenSaved = false;
      await ContactsCustomPlugin.saveUserContactsAsVcf().then((result) => {
        contactsHasBeenSaved = result["saved"];
      });

      if (contactsHasBeenSaved) {
        this.fakeProgressionStatusValue(30, "Restoration point created!");
        let contactsHasBeenUpdated = false;
        await ContactsCustomPlugin.updateUserContacts().then((result) => {
          this.progressionStatus = "Updating contacts...";
          contactsHasBeenUpdated = result["updated"];
        });

        if (contactsHasBeenUpdated) {
          this.fakeProgressionStatusValue(
            100,
            "Contacts has been updated successfully!"
          );
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
      this.progressionHandler = setInterval(
        this.incrementProgressionStatusValue,
        100
      );
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
      await ContactsCustomPlugin.openRestorationPointFile().then((result) => {
        if (result["opened"]) {
          ContactsCustomPlugin.deleteUserContacts().then((result) => {
            if (result["deleted"]) {
              console.log("all contacts has been deleted");
            } else {
              console.log("deletion failed");
            }
          });
        } else {
          console.log("has not been opened");
        }
      });
    },


    async RestoreIfRestorationPointExists() {
      const alert = await alertController
        .create({
          cssClass: 'my-custom-class',
          header: 'Restoration',
          message: 'Cette action effacera tous vos contacts',
          buttons: [
            {
              text: 'Cancel',
              role: 'cancel',
              cssClass: 'secondary',
              handler: blah => {
                console.log('Confirm Cancel:', blah)
              },
            },
            {
              text: 'Okay',
              handler: () => {
                this.restoreAllContact();
              },
            },
          ],
        });
      return alert.present();
    },


    async NotRestorationPointExists() {
      const alert = await alertController
        .create({
          cssClass: 'my-custom-class',
          header: 'Sauvegarde',
          message: "Auccun point de restauration n'a ete trouver !" ,
          buttons: ['OK'],
        });
      return alert.present();
    },

    
  },
};
</script>

<style >

@font-face {
	font-family: 'Maven Pro';
	src: url('/assets/MavenPro-Medium.ttf'), url('/assets/MavenPro-Regular.ttf');
}
.btn-wrapper {
  margin: 8px 25px;
  text-align: center; /* center inline and inline-block element inside */
}

*{
  font-family: 'Maven Pro' !important;
}

.OB-toolbar {
  width: 100%;
  height: 56px !important;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0px 32px;
}

.OB-toolbar .OB-toolbar-img {
  height: 24px;
}

.OB-toolbar-img-restore {
  display: flex;
  justify-content: center;
  align-items: center;
}

.OB-toolbar-img-restore img {
  height: 32px;
}

.OB-Container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  padding: 0px 32px;
}

.OB-Container .OB-numberOfContacts {
  margin: 0px 0px 16px 0px;
}

.OB-Container .OB-numberOfContacts span {
  font-size: 18px !important;
  color: #182b3a !important;
  height: 24px !important;
  font-weight: 400;
}

.OB-Container .OB-progressionEvolution {
  display: flex;
}

.progressionEvolution-shadow {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #c1d9ff !important;
  border-radius: 100%;
  width: 8em;
  height: 8em;
}

.progressionEvolution-ligth {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #3880ff !important;
  border-radius: 100%;
  width: 6.5em;
  height: 6.5em;
}

.progressionEvolution-ligth span {
  font-size: 24px !important;
  color: white !important;
  font-weight: 600;
}

.OB-progressionStatus {
  font-size: 16px !important;
  color: #D84685 !important;
  font-weight: 600;
  margin: 24px 0px 32px 0px;
  opacity: 0.8;
}

.OB-Container .OB-btn .OB-btn-Coversion {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16px;
  column-gap: 8px;
  background-color: #3880ff !important;
  border-radius: 4px;
}

.OB-Container .OB-btn .OB-btn-Coversion span {
  font-size: 18px !important;
  color: white !important;
  font-weight: 600;
}

.OB-Container .OB-btn .OB-btn-Coversion img {
  height: 24px !important;
}





@media screen and (min-height: 730px) and (max-height: 2000px) {
.progressionEvolution-shadow {
  width: 10em !important;
  height: 10em !important;
}
  .progressionEvolution-ligth {
  width: 8.5em !important;
  height: 8.5em !important;
}

.progressionEvolution-ligth span {
  font-size: 32px !important;
}

.OB-progressionStatus {
  font-size: 18px !important;
}

.OB-Container .OB-numberOfContacts span {
  font-size: 20px !important;
}

.OB-Container .OB-btn .OB-btn-Coversion span {
  font-size: 20px !important;
}


}
</style>

/*
* ionic build
* npx cap open android | ouvrir android studio
* npx cap init [appName] [appId] | permet de definir le nom et l'identifiant de l'application
* npx cap sync to update custom plugins
* npx cap copy | Synchroniser l'application avec capicitor
*/