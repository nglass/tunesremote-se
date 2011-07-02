package net.firefly.client.model.data;

public class RadioStation extends Song {
   
   private static final long serialVersionUID = -5576478333136187455L;

   protected String description;

   public RadioStation() {
      super();
   }

   public String getDescription() {
      return description;
   }

   public void setDescription(String description) {
      this.description = description;
   }
}
