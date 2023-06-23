package com.example.firebaseexample.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.navigation.Navigation.findNavController
import com.example.firebaseexample.HomeFragmentDirections
import com.example.firebaseexample.Models.Contacts
import com.example.firebaseexample.databinding.RvContactsItemBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class RvContactsAdapter(private val contactsList: ArrayList<Contacts>): RecyclerView.Adapter<RvContactsAdapter.ViewHolder>() {

    class ViewHolder(val binding: RvContactsItemBinding): RecyclerView.ViewHolder(binding.root) {
        
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(RvContactsItemBinding.inflate(LayoutInflater.from(parent.context),
            parent,false))
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = contactsList[position]
        holder.apply {
            binding.apply {
                tvNameItem.text = currentItem.name
                tvIdItem.text = currentItem.id
                tvPhoneItem.text = currentItem.phone
                Picasso.get().load(currentItem.imgUri).into(imgItem)
                rvContainer.setOnClickListener {
                    val action = HomeFragmentDirections.actionHomeFragmentToUpdateFragment(
                        currentItem.id.toString(),
                        currentItem.name.toString(),
                        currentItem.phone.toString(),
                        currentItem.imgUri.toString()
                    )
                    findNavController(holder.itemView).navigate(action)
                }
                rvContainer.setOnLongClickListener {
                    MaterialAlertDialogBuilder(holder.itemView.context)
                        .setTitle("Delete contact?")
                        .setMessage("Are you sure you want to delete this item?")
                        .setPositiveButton("Yes"){_,_ ->
                            val firebaseRef = FirebaseDatabase.getInstance().getReference("Contacts")
                            val storageRef = FirebaseStorage.getInstance().getReference("Images")
                            storageRef.child(currentItem.id.toString()).delete()
                            firebaseRef.child(currentItem.id.toString()).removeValue()
                                .addOnSuccessListener {
                                    Toast.makeText(holder.itemView.context, "Contact deleted successfully", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(holder.itemView.context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
                                }
                        }
                        .setNegativeButton("No"){_,_ ->
                            Toast.makeText(holder.itemView.context, "Canceled", Toast.LENGTH_SHORT).show()
                        }
                        .show()
                    return@setOnLongClickListener true
                }
            }
        }
    }
}