<template>
  <ion-page class="swiper">
    <ion-header>
      <ion-toolbar>
        <div class="OB-toolbar">
          <img class="OB-toolbar-img" src="/assets/icon/Logo-8to10.svg" />
          <div class="OB-toolbar-img-restore">
          </div>
        </div>
      </ion-toolbar>
    </ion-header>
    <ion-content  :fullscreen="true">
      <div class="About ">

        <div class="About-option">
          <span class="About-option-heading"> A propos | OutBox </span>
        </div>

        <div class="About-text">
          <span
            >Nous sommes deux développeurs passionnés et friands de défis qui
            ont appris avec enthousiasme le lancement d'un nouveau plan de
            numérotation qui venait résoudre un problème de saturation en
            numéros mobile de certains opérateurs.</span
          >
          <span
            >Après avoir vu l'annonce qui dictait les nouvelles consigne de
            numérotation ainsi que la mise en garde de l'ARTCI suivant laquelle
            nous développeurs d'applications devrions respecter les dispositions
            légales relatives à la protection des données à caractère personnel
            ainsi que les principes du nouveau plan de numérotation à dix (10)
            chiffres.
          </span>
          <span
            >Nous avons conçu une application qui fonctionne totalement en local
            ce qui signifie qu'il ne communique aucunement avec un serveur
            externe, respectant ainsi les règles sus-mentionnées.
          </span>
          <span
            >En effet, avec 8to10 plus besoin de vous soucier du passage de vos
            numéros de téléphone de huit à dix chiffres, en un seul clique et de
            manière instantanée, tous vos contacts passent à dix chiffres.
          </span>
        </div>

        <div class="About-option">
          <span class="About-option-heading"> Offrez-nous un café </span>
          <a>https://www.buymeacoffee/8to10.com</a>
          <span>57357748(OM) - 65119921(MOMO )</span>
        </div>

        <div class="About-option">
          <span class="About-option-heading"> Contactez-nous </span>
          <span>Email : out-box@contact.com</span>
        </div>

        <div class="About-infos">
          <span> Gratuit </span>
          <span> Version 1.9.12 </span>
          <span> Proposée par OutBox </span>
        </div>
      </div>
    </ion-content>
  </ion-page>
</template>

<script>
import { IonPage, IonContent, IonHeader, IonToolbar } from "@ionic/vue";

export default {
  name: "tab3",
  components: { IonContent, IonPage, IonHeader, IonToolbar },
};
</script>

<style >




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
.About {
  display: flex;
  flex-direction: column;
  padding: 32px 24px;
  row-gap: 32px;
}

.About .About-text {
  display: flex;
  flex-direction: column;
  row-gap: 16px;
}
.About .About-text span {
  position: relative;
  line-height: 24px;
  font-size: 16px;
  font-weight: 400;
  color: #182b3a;
}

.About .About-option {
  display: flex;
  flex-direction: column;
  row-gap: 8px;
}

.About .About-option .About-option-heading {
  font-size: 24px !important;
  font-weight: 700;
  color: #182b3a;
}

.About .About-option span {
  font-size: 16px;
  font-weight: 400;
  color: #182b3a;
}

.About .About-infos {
  display: inline-flex;
  flex-direction: column;
  justify-items: center;
  align-items: center;
  margin: 48px 0px !important;
}

.About .About-infos span {
  padding-bottom: 8px;
  font-size: 14px !important;
  font-weight: 500;
  color: #182b3a;
  opacity: 0.7;
}
</style>