import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { GeolocationService} from '@ng-web-apis/geolocation'; //https://github.com/ng-web-apis/geolocation

import { debounceTime, tap, switchMap, finalize, distinctUntilChanged, filter, startWith } from 'rxjs/operators';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-form',
  templateUrl: './form.component.html',
  styleUrls: ['./form.component.css']
})
export class FormComponent implements OnInit {

  // autocomplete
  searchKwCtrl = new FormControl();
  filteredKws: Observable<any>;
  
  //checkbox
  public disabled = false
  public checkboxStatus:any=false
  //transfered data
  public formInfo:any={
    keyword_val : "",
    distance_val : "10",
    categoary_val : "Default",
    location_val : "",
    autoloc : "false",
    latitude : "",
    longitude : ""
  };
  submit_times = 0;

  resulthidden:boolean = true;
  noresulthidden:boolean = true;

  public search_result = []; //Array<Map<String,String>>
  public bussiness_to_load:any = 0;
  
  // https://stackoverflow.com/questions/57852645/angular-material-autocomplete-from-api
  constructor(private readonly geolocation$: GeolocationService) {
    this.filteredKws = this.searchKwCtrl.valueChanges
        .pipe(
          startWith(''),
          debounceTime(500),
          distinctUntilChanged(),
          switchMap(val => {
            this.formInfo.keyword_val = val;
            return this.filter(val || '');
          })       
        );
  }

  ngOnInit(): void {   
     
  }
    
  // filter and return the values
  async filter(val: string): Promise<Observable<any[]>> {
    const back_resp = await fetch('https://businessyelpsearch.wl.r.appspot.com/atcpt?&'+ 'typedWord' +'=' + val).then(response => response.json());
    return back_resp.suggestions;
  }

  curLocation(): void{
    this.checkboxStatus = ! this.checkboxStatus
    if (this.checkboxStatus == true) {
      this.geolocation$.subscribe(position => this.showPosition(position));
      this.formInfo.autoloc = true;
      this.disabled = true;
    }else {
      this.formInfo.autoloc = false;
      this.disabled = false;
    }
  }
  showPosition(position:any): void{
    this.formInfo.latitude = position.coords.latitude;
    this.formInfo.longitude = position.coords.longitude;
    //console.log(position.coords.latitude);
    //console.log(position.coords.longitude);
  }
  stringifyParams() {
    var urlParams = '?';
    for (let key in this.formInfo) {
      var value = this.formInfo[key];
      urlParams += '&' + key + '=' + value;
    }
    return encodeURI(urlParams);
	}

  async submit_search(el: HTMLElement): Promise<void>{
    //const url = 'http://localhost/search';
    const url = 'https://businessyelpsearch.wl.r.appspot.com/search';
    this.search_result = await fetch(url+this.stringifyParams()).then(response => response.json());
    this.bussiness_to_load = Object.keys(this.search_result).length;
    if (this.bussiness_to_load==0){
      this.resulthidden = true;
      this.noresulthidden = false;
    }else{
      this.resulthidden = false;
      this.noresulthidden = true;
      el.scrollIntoView();
    }
    this.submit_times += 1;
  }

  // reset_search clear information inside the formInfo, revert checkbox and disabled input
  reset_search(): void{
    this.formInfo.keyword_val = "";
    this.formInfo.distance_val = "10";
    this.formInfo.categoary_val = "Default";
    this.formInfo.location_val = "";
    this.formInfo.autoloc = "false";
    this.formInfo.latitude = "";
    this.formInfo.longitude = "";
    this.checkboxStatus = false;
    this.disabled = false;
    this.resulthidden = true;
    this.noresulthidden = true;
    this.searchKwCtrl.reset();
  }
}
