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
    <ion-content  :fullscreen="true">
      <div class="OB-Container">
        

        <div class="OB-progressionEvolution">
          <div class="progressionEvolution-shadow ">
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
          <ion-button size="large">
            <div v-on:click="updateAllContact" class="OB-btn-Coversion">
            <img src="/assets/icon/rocket-outline.svg" alt="" />
            <span> Convertir </span>
          </div>
          </ion-button>
          
        </div>
      </div>

  
    </ion-content>
  </ion-page>
</template>

<script>
import { IonPage, IonHeader, IonToolbar, IonContent, IonButton, alertController, toastController } from "@ionic/vue";
import { Plugins } from "@capacitor/core";
import { refreshCircle } from "ionicons/icons";

const { ContactsCustomPlugin } = Plugins;

export default {
  name: "Conversion",
  components: { IonHeader, IonToolbar, IonContent, IonButton, IonPage },

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
      document.querySelector('.progressionEvolution-shadow').classList.add('progressAnnimate');
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
          document.querySelector('.progressionEvolution-shadow').classList.remove('progressAnnimate');
          this.Actionstoast("Contacts has been updated successfully!", 'success')
        } else {
          this.progressionStatus = "Contacts Update failed !";
          this.Actionstoast("Contacts Update failed !", 'error')
        }
      } else {
        this.progressionStatus = "Restoration point failed!";
        this.Actionstoast("Restoration point failed!", 'error')
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
              // console.log("all contacts has been deleted");
              this.Actionstoast("all contacts has been deleted", 'success')
            } else {
              // console.log("deletion failed");
              this.Actionstoast("deletion failed", 'error')
            }
          });
        } else {
          // console.log("has not been opened");
          this.Actionstoast("has not been opened", 'error')
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


    async Actionstoast(message, options) {
      const toast = await toastController
        .create({
          message: message,
          duration: 2000,
          cssClass: `toast-${options}`
        })
      return toast.present();
    },

    
  },
};
</script>

<style >

.toast-success{
  --background: #71C9CE !important;
}
.toast-error{
  --background: #D84685 !important;
}

@font-face {
	font-family: 'Maven Pro';
	src: url('/assets/MavenPro-Medium.ttf'), url('/assets/MavenPro-Regular.ttf');
}


*{
  font-family: 'Maven Pro' !important;
}

.progressAnnimate{
  width: 10em !important;
  height: 10em !important;
  background-color:#b9e6e9 !important;
  animation: mymove 4s infinite;
  clip-path: circle(50%);
}

@keyframes mymove {
  50% {clip-path: circle(35%);}
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
  height: 32px;
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
  background-color:#b9e6e9 !important;
  border-radius: 100%;
  width: 8em;
  height: 8em;
}

.progressionEvolution-ligth {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color:#71c9ce !important;
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
  color: #71c9ce !important;
  font-weight: 600;
  margin: 24px 0px 32px 0px;
  opacity: 0.9;
}

.OB-Container .OB-btn .OB-btn-Coversion {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16px;
  column-gap: 8px;
  background-color:#ffb74d !important;
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

.progressAnnimate{
  width: 12em !important;
  height: 12em !important;
  background-color:#b9e6e9 !important;
  animation: mymove 4s infinite;
  clip-path: circle(50%);
}
@keyframes mymove {
  50% {clip-path: circle(40%);}
}
.progressionEvolution-shadow {
  width: 11em ;
  height: 11em ;
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