<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <div class="OB-toolbar">
          <div class="">
            <img class="OB-toolbar-img" src="/assets/icon/Logo-8to10.svg" />
          </div>
          <div v-if="updateAllContact__start == true" class="OB-toolbar-img-restore">
            <img class="" src="/assets/icon/refresh-circle.svg" />
            <ion-label> Huits Chiffres </ion-label>
          </div>
          <div v-if="updateAllContact__start == false"  v-on:click="RestoreIfRestorationPointExists" class="OB-toolbar-img-restore">
            <img class="RestoreClass" src="/assets/icon/refresh-circle.svg" />
            <ion-label> Huits Chiffres </ion-label>
          </div>
        </div>
      </ion-toolbar>
    </ion-header>
    <ion-content  :fullscreen="true">
      <div class="OB-Container swiper">
        
        
        <div class="OB-progressionEvolution">
          <div class="progressionEvolution-shadow ">
            <div class="progressionEvolution-ligth">
              <span>{{ progressionStatusValue }}%</span>
            </div>
          </div>
        </div>

        <div class="OB-progressionStatus">
          <span>{{
            progressionStatus
              ? progressionStatus
              : "Appuyer pour commencer"
          }} </span>
        </div>

        <div class="OB-numberOfContacts">
          <span>{{ numberOfContacts }} numéro(s) de téléphone</span>
        </div>

        <div class="OB-btn" >
          <ion-button class="annimateButton ShowButtonAnnimate" v-if="updateAllContact__start == false" size="large">
            <div v-on:click="updateAllContact" class="OB-btn-Coversion">
            <img src="/assets/icon/rocket-outline.svg" alt="" />
            <span> Convertir </span>
          </div>
          </ion-button>

          <!-- <ion-button class="annimateButton ShowButtonAnnimate" v-if="updateAllContact__start == false" size="large">
            <div v-on:click="updateAllContact" class="OB-btn-Coversion">
            <img src="/assets/icon/rocket-outline.svg" alt="" />
            <span> Convertir </span>
          </div>
          </ion-button>
           -->
        </div>
      </div>

  
    </ion-content>
  </ion-page>
</template>

<script>
import { IonPage, IonHeader, IonToolbar, IonContent, IonLabel, IonButton, alertController, toastController } from "@ionic/vue";
import { Plugins } from "@capacitor/core";
import { refreshCircle } from "ionicons/icons";


const {ContactsCustomPlugin} = Plugins;

export default {

  name: "Conversion",
  components: {IonHeader, IonToolbar, IonLabel, IonButton, IonContent, IonPage},


  data() {
    return {
      refreshCircle,

      numberOfContacts: 0,

      progressionStatus: "",
      progressionStatusValue: 0,
      progressionCurrentLimit: 0,
      progressionEndMessage: "",
      progressionHandler: null,

      updateAllContact__start: false,
      // restorationPointExists: null,

    }
  },

  mounted() {
    this.countPhoneNumbers();
    // this.checkIfRestorationPointExists()
  },

  methods: {
    countPhoneNumbers() {
      ContactsCustomPlugin.countPhoneNumbers().then(result => {
        this.numberOfContacts = result["count"];
      })
    },

    // checkIfRestorationPointExists() {
    //   ContactsCustomPlugin.restorationPointExists().then((result) => {
    //     this.restorationPointExists = result["exists"];
    //     console.log(this.restorationPointExists);
    //   });
    // },

    async updateAllContact() {
      this.resetProgression();

      document.querySelector('.annimateButton').classList.remove('ShowButtonAnnimate');
      document.querySelector('.annimateButton').classList.add('HideButtonAnnimate');
      setTimeout(() => {
        this.updateAllContact__start = true;
      }, 530)
      document.querySelector('.progressionEvolution-shadow').classList.add('progressAnnimate');

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
          document.querySelector('.progressionEvolution-shadow').classList.remove('progressAnnimate');
          this.progressionStatus = "La mise à jour a échoué !";
          this.Actionstoast("La mise à jour a échoué !", "error");
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

      document.querySelector('.annimateButton').classList.remove('ShowButtonAnnimate');
      document.querySelector('.annimateButton').classList.add('HideButtonAnnimate');
      setTimeout(() => {
        this.updateAllContact__start = true;
      }, 530)
      document.querySelector('.progressionEvolution-shadow').classList.add('progressAnnimate');

      let hasBeenRestored = false;
      this.fakeProgressionStatusValue(50)
      this.progressionStatus = "Recherche la sauvegarde...";
      this.updateAllContact__start = true;
      await ContactsCustomPlugin.undoUpdateUserContacts().then(result => {
          this.progressionStatus = "Restauration en cours ...";
          hasBeenRestored = result["restored"];
      });

      if(hasBeenRestored){
        this.fakeProgressionStatusValue(100, "Les numéros de téléphone ont été parfaitement mis à jour!");
        this.updateAllContact__start = false;
        
      document.querySelector('.progressionEvolution-shadow').classList.remove('progressAnnimate');
        this.countPhoneNumbers();
      }else{
        this.progressionStatus = "Echec, accune sauvegarde trouver !";
        this.Actionstoast("La restauration a échoué !", "error");
      }
        

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


    async NotRestorationPointExists() {

      document.querySelector('.RestoreClass').classList.add('_EventClass-rotate');

      setTimeout( async() => {
        const alert = await alertController
        .create({
          cssClass: 'my-custom-class',
          header: 'Sauvegarde',
          message: "Aucun point de restauration a été trouvé !" ,
          buttons: ['Fermer'],
        });
        document.querySelector('.RestoreClass').classList.remove('_EventClass-rotate');
      return alert.present();
      }, 500)
    },


    async RestoreIfRestorationPointExists() {
      const alert = await alertController
        .create({
          cssClass: 'my-custom-class',
          header: 'Restauration',
          message: 'Cette action effacera tous vos numéros de téléphone, dans l\'attente de la sauvegarde de votre contacts !',
          buttons: [
            {
              text: 'Annuler',
              role: 'cancel',
              cssClass: 'secondary',
              handler: blah => {
                console.log('Confirm Cancel:', blah)
              },
            },
            {
              text: 'Confirmer',
              handler: () => {
                this.restoreAllContact();
              },
            },
          ],
        });
      return alert.present();
    },

  }

  
}
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

.ShowButtonAnnimate{
  background-color:#b9e6e9 !important;
  animation: ShowButton 0.5s normal;
  opacity: 1;
}

@keyframes ShowButton {
  0%{opacity: 0;}
  50% {opacity: 0.5;}
  75% {opacity: 0.7;}
  100% {opacity: 1;}
}

.HideButtonAnnimate{
  background-color:#b9e6e9 !important;
  animation: HideButton 0.5s normal;
  opacity: 0;
}
@keyframes HideButton {
  0%{opacity: 1;}
  50%{opacity: 0.5;}
  100% {opacity: 0;}
}

.swiper{
  animation: swiperAnnimate 0.5s normal;
  position: relative;
  top: 0px;
  opacity: 1;
}

@keyframes swiperAnnimate {
  0%{opacity: 0; top: 60px;}
  100% {opacity: 1; top: 0px;}
}
._EventClass-rotate{
  transform: rotate(0deg) ;
  animation: rotateAnnimate 0.5s normal;
}
@keyframes rotateAnnimate {
  0%{transform: rotate(180deg) ;}
  100% {opacity: 1; top: 0px;}
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
  column-gap: 4px;
}

.OB-toolbar-img-restore img {
  height: 24px;
}

.OB-toolbar-img-restore span {
  font-size: 16px !important;
  color: #182b3a !important;
  font-weight: 500;
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
  text-align: center !important;
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