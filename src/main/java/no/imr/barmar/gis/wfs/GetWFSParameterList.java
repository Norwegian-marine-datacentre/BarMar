package no.imr.barmar.gis.wfs;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import no.imr.barmar.gis.sld.StAXreader;

import org.springframework.stereotype.Component;

@Component
public class GetWFSParameterList extends StAXreader {

    private List<String> theList = new ArrayList<String>();

    @Override
    public void onElementStart(String element) throws XMLStreamException {
        if (element.equalsIgnoreCase(tagtolookfor)) {
            String value = getCurrentElementValue();
            if (!theList.contains(value)) {
                theList.add(value);
            }
        }
    }

    public void deleteList(){
        theList = new ArrayList<String>();
    }

    public List<String> getList() {
        return theList;
    }

    void setFilter(String filter) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
