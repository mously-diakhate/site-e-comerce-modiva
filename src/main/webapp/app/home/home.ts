import { Component, computed, inject } from '@angular/core';

import { AccountService } from 'app/core/auth/account.service';

import AdminDashboard from './admin-dashboard/admin-dashboard';
import ShopHome from './shop-home/shop-home';

@Component({
  selector: 'jhi-home',
  templateUrl: './home.html',
  imports: [AdminDashboard, ShopHome],
})
export default class Home {
  private readonly accountService = inject(AccountService);

  readonly account = this.accountService.account;

  /** Seules les administratrices voient le tableau de bord ; tout le reste va sur la boutique publique. */
  readonly isAdmin = computed(() => this.accountService.hasAnyAuthority(['ROLE_ADMIN']));
}
