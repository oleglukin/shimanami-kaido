package shimanamikaido.api.model;

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


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        sb.append("\"id_sample\":\"" + id_sample + "\",");
        sb.append("\"num_id\":\"" + num_id + "\",");
        sb.append("\"id_location\":\"" + id_location + "\",");
        sb.append("\"id_signal_par\":\"" + id_signal_par + "\",");
        sb.append("\"id_detected\":\"" + id_detected + "\",");
        sb.append("\"id_class_det\":\"" + id_class_det + "\"");
        sb.append("}");
        return sb.toString();
    }
}