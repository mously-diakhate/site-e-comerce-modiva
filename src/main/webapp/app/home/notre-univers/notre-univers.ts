import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

interface Atout {
  icon: string;
  titre: string;
  description: string;
}

/**
 * Page "Notre univers" : hero photo + baseline, texte de présentation de la
 * marque, trois atouts (soin du détail / matières / proximité), puis un
 * bandeau d'appel à l'action vers la boutique.
 */
@Component({
  selector: 'jhi-notre-univers',
  templateUrl: './notre-univers.html',
  styleUrl: './notre-univers.scss',
  imports: [RouterLink, FontAwesomeModule],
})
export default class NotreUnivers {
  /**
   * Photo de fond du hero — laissez vide tant que vous n'avez pas déposé de
   * photo dans src/main/webapp/content/images/notre-univers/. Un dégradé de
   * couleur s'affiche à la place tant que c'est vide (voir notre-univers.scss).
   */
  readonly heroImageUrl = 'content/images/notre-univers-hero.jpg';

  readonly atouts: Atout[] = [
    {
      icon: 'gem',
      titre: 'Le soin du détail',
      description: 'Chaque coupe est étudiée pour flatter la silhouette et durer dans le temps.',
    },
    {
      icon: 'leaf',
      titre: 'Des matières justes',
      description: 'Des tissus choisis pour le climat sénégalais : légers, respirants, confortables.',
    },
    {
      icon: 'heart',
      titre: 'La proximité',
      description: 'Un conseil personnalisé, du choix de la taille à la livraison chez vous.',
    },
  ];
}
