import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BookingComponent } from './components/booking/booking.component';
import { FormComponent } from './components/form/form.component';

const routes: Routes = [
  {
    path:'search',component: FormComponent
  },
  {
    path:'bookings',component: BookingComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
