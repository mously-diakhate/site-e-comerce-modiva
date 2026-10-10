import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

interface ContactInfo {
  icon: string;
  text: string;
}

/**
 * Page "Contact" : présentation + liste d'infos pratiques (WhatsApp, e-mail,
 * adresse, horaires — textes "à compléter" en attendant les vraies
 * coordonnées de la boutique) et un formulaire de contact.
 */
@Component({
  selector: 'jhi-contact',
  templateUrl: './contact.html',
  styleUrl: './contact.scss',
  imports: [FormsModule, FontAwesomeModule],
})
export default class Contact {
  // ---------------------------------------------------------------------
  // TODO : remplacez ces textes par les vraies coordonnées de la boutique
  // (numéro WhatsApp, adresse e-mail, horaires) une fois disponibles.
  // ---------------------------------------------------------------------
  readonly infos: ContactInfo[] = [
    { icon: 'phone', text: 'À compléter avec votre numéro WhatsApp' },
    { icon: 'envelope', text: 'À compléter avec votre adresse e-mail' },
    { icon: 'location-dot', text: 'Dakar, Sénégal — livraison dans toutes les régions' },
    { icon: 'clock', text: 'À compléter avec vos horaires' },
  ];

  nom = '';
  telephone = '';
  email = '';
  message = '';

  submitContact(): void {
    // TODO : brancher sur un endpoint d'envoi de message une fois disponible
    // côté backend (par exemple un ContactService qui poste vers /api/contacts).
    this.nom = '';
    this.telephone = '';
    this.email = '';
    this.message = '';
  }
}
