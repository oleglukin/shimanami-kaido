package sinologic16.api.model;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class SignalEvent {
    public String id_sample; // item identifier (76rtw)
    public String num_id; // item serial number (ffg#er111)
    public String id_location; // location from (3211.2334), can be a name
    public String id_signal_par; // sensor generating signal (0xcv11cs)
    public String id_detected; // status data (None), - functional, (Nan), - failed
    public String id_class_det; // failure type (req11)
}