<template>
  <ion-page>
    <ion-header>
      <ion-toolbar>
        <div class="OB-toolbar">
          <div class="">
            <img class="OB-toolbar-img" src="/assets/icon/Logo-8to10.svg" alt="logo"/>
          </div>
          <transition name="bounce">
            <div v-if="!isLoading" v-on:click="RestoreIfRestorationPointExists" class="OB-toolbar-img-restore">
              <img class="RestoreClass" src="/assets/icon/refresh-circle.svg" alt="refresh icon"/>
              <ion-label> Huit Chiffres</ion-label>
            </div>
          </transition>
        </div>
      </ion-toolbar>
    </ion-header>
    <ion-content :fullscreen="true">
      <div class="OB-Container swiper">


        <div class="OB-progressionEvolution">
          <div class="progressionEvolution-shadow" v-bind:class="{ 'progressAnnimate': isLoading}">
            <div class="progressionEvolution-ligth">
              <span>{{ progressionStatusValue }}%</span>
            </div>
          </div>
        </div>

        <div class="OB-progressionStatus">
          <span>{{ progressionStatus }} </span>
        </div>

        <div class="OB-numberOfContacts">
          <span>{{ numberOfContacts }} numéro(s) de téléphone</span>
        </div>

        <div class="OB-btn">
          <transition name="bounce">
            <ion-button v-on:click="updateAllContact" class="btn-convert" :disabled="isLoading" v-if="!isLoading">
              <img src="/assets/icon/rocket-outline.svg" height="18" width="18" alt="rocket"/>
              <span style="margin-left: 8px;"> Convertir </span>
            </ion-button>
          </transition>
        </div>

      </div>

    </ion-content>
  </ion-page>
</template>

<script>
import {
  IonPage,
  IonHeader,
  IonToolbar,
  IonContent,
  IonLabel,
  IonButton,
  alertController,
  toastController
} from "@ionic/vue";
import {Plugins} from "@capacitor/core";
import {refreshCircle} from "ionicons/icons";


const {ContactsCustomPlugin} = Plugins;

export default {

  name: "Conversion",
  components: {IonHeader, IonToolbar, IonLabel, IonButton, IonContent, IonPage},


  data() {
    return {
      refreshCircle,

      numberOfContacts: 0,

      progressionStatus: "Appuyer sur un bouton pour commencer",
      progressionStatusValue: 0,
      progressionCurrentLimit: 0,
      progressionEndMessage: "",
      progressionHandler: null,

      isLoading: false,
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

      this.isLoading = true;
      let contactsHasBeenUpdated = false;
      this.progressionStatus = "Mise à jour des numéros de téléphone en cours de progression ..."
      this.fakeProgressionStatusValue(90, "Encore quelques secondes s'il vous plaît  🙏 ");
      ContactsCustomPlugin.updateUserContacts().then(result => {
        contactsHasBeenUpdated = result["updated"];

        if (contactsHasBeenUpdated) {
          this.fakeProgressionStatusValue(100, "Les numéros de téléphone ont été parfaitement mis à jour 😎");
          this.isLoading = false;
        } else {
          this.progressionStatus = "La mise à jour a échoué 😔";
          this.Actionstoast("Échec de la mis à jour, réessayer !", "error");
          this.isLoading = false;
        }

      });


    },


    resetProgression() {
      this.progressionStatusValue = 0;
    },

    async fakeProgressionStatusValue(limit, message) {
      this.progressionCurrentLimit = limit;
      this.progressionEndMessage = message;
      this.progressionHandler = setInterval(this.incrementProgressionStatusValue, 200);
    },

    async incrementProgressionStatusValue() {
      if (this.progressionStatusValue < this.progressionCurrentLimit)
        this.progressionStatusValue += 1;
      else {
        clearInterval(this.progressionHandler);
        this.progressionStatus = this.progressionEndMessage;
      }

    },

    async restoreAllContact() {
      this.resetProgression();

      this.isLoading = true;
      let hasBeenRestored = false;
      this.progressionStatus = "Restauration des numéros de téléphone en cours de progression ...";
      this.fakeProgressionStatusValue(90, "Encore quelques secondes s'il vous plaît 🙏");
      ContactsCustomPlugin.undoUpdateUserContacts().then(result => {
        hasBeenRestored = result["restored"];

        if (hasBeenRestored) {
          this.fakeProgressionStatusValue(100, "Les numéros de téléphone ont été parfaitement restaurés 😎");
          this.isLoading = false;
        } else {
          this.progressionStatus = "La restauration a échoué 😔";
          this.Actionstoast("Échec de la restauration, réessayez !", "error");
          this.isLoading = false;
        }
      });


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


    async RestoreIfRestorationPointExists() {
      const alert = await alertController
          .create({
            cssClass: 'my-custom-class',
            header: 'Restauration',
            message: 'Cette action ramènera tous vos numéros de téléphone à huit chiffres !',
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

<style>


@font-face {
  font-family: 'Maven Pro';
  src: url('/assets/MavenPro-Medium.ttf'), url('/assets/MavenPro-Regular.ttf');
}


* {
  font-family: 'Maven Pro', 'Roboto', sans-serif !important;
}


.btn-convert {
  height: 45px;
}


.bounce-enter-active {
  animation: bounce-in .5s;
}

.bounce-leave-active {
  animation: bounce-in .5s reverse;
}

@keyframes bounce-in {
  0% {
    transform: scale(0);
  }
  50% {
    transform: scale(1.5);
  }
  100% {
    transform: scale(1);
  }
}

.progressAnnimate {
  width: 10em !important;
  height: 10em !important;
  background-color: #b9e6e9 !important;
  animation: mymove 4s infinite;
  clip-path: circle(50%);
}

@keyframes mymove {
  50% {
    clip-path: circle(35%);
  }
}

.ShowButtonAnnimate {
  background-color: #b9e6e9 !important;
  animation: ShowButton 0.5s normal;
  opacity: 1;
}

@keyframes ShowButton {
  0% {
    opacity: 0;
  }
  50% {
    opacity: 0.5;
  }
  75% {
    opacity: 0.7;
  }
  100% {
    opacity: 1;
  }
}

.HideButtonAnnimate {
  background-color: #b9e6e9 !important;
  animation: HideButton 0.5s normal;
  opacity: 0;
}

@keyframes HideButton {
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
  100% {
    opacity: 0;
  }
}

.swiper {
  animation: swiperAnnimate 0.5s normal;
  position: relative;
  top: 0;
  opacity: 1;
}

@keyframes swiperAnnimate {
  0% {
    opacity: 0;
    top: 60px;
  }
  100% {
    opacity: 1;
    top: 0;
  }
}

._EventClass-rotate {
  transform: rotate(0deg);
  animation: rotateAnnimate 0.5s normal;
}

@keyframes rotateAnnimate {
  0% {
    transform: rotate(180deg);
  }
  100% {
    opacity: 1;
    top: 0;
  }
}


.OB-toolbar {
  width: 100%;
  height: 56px !important;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 32px;
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
  padding: 0 32px;
}


.OB-Container .OB-numberOfContacts {
  margin: 0 0 16px 0;
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
  background-color: #b9e6e9 !important;
  border-radius: 100%;
  width: 8em;
  height: 8em;
}

.progressionEvolution-ligth {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #71c9ce !important;
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
  margin: 24px 0 5px 0;
  text-align: center !important;
  height: 90px;
}

.OB-Container .OB-btn .OB-btn-Coversion {
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 16px;
  column-gap: 8px;
  background-color: #ffb74d !important;
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

  .progressAnnimate {
    width: 12em !important;
    height: 12em !important;
    background-color: #b9e6e9 !important;
    animation: mymove 4s infinite;
    clip-path: circle(50%);
  }

  @keyframes mymove {
    50% {
      clip-path: circle(40%);
    }
  }
  .progressionEvolution-shadow {
    width: 11em;
    height: 11em;
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
